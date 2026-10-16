package com.anarsi.client.config;

import com.anarsi.client.AnarsiClient;
import com.anarsi.client.hack.Hack;
import com.anarsi.client.hack.HackManager;
import com.anarsi.client.setting.BoolSetting;
import com.anarsi.client.setting.NumberSetting;
import com.anarsi.client.setting.Setting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/** Tuşları, açık/kapalı durumları ve ayarları .minecraft/config/anarsiclient.json'a kaydeder. */
public final class AnarsiConfig {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

   private AnarsiConfig() {
   }

   private static Path file() {
      return FabricLoader.getInstance().getConfigDir().resolve("anarsiclient.json");
   }

   public static void save() {
      JsonObject root = new JsonObject();
      for (Hack h : HackManager.all()) {
         JsonObject o = new JsonObject();
         o.addProperty("enabled", h.isEnabled());
         o.addProperty("key", h.getKey());
         JsonObject settings = new JsonObject();
         for (Setting s : h.getSettings()) {
            if (s instanceof NumberSetting n) settings.addProperty(s.getName(), n.get());
            else if (s instanceof BoolSetting b) settings.addProperty(s.getName(), b.get());
         }
         o.add("settings", settings);
         root.add(h.getName(), o);
      }
      try {
         Files.createDirectories(file().getParent());
         try (Writer w = Files.newBufferedWriter(file(), StandardCharsets.UTF_8)) {
            GSON.toJson(root, w);
         }
      } catch (Exception e) {
         AnarsiClient.LOGGER.error("Config kaydedilemedi", e);
      }
   }

   public static void load() {
      Path f = file();
      if (!Files.exists(f)) return;
      try (Reader r = Files.newBufferedReader(f, StandardCharsets.UTF_8)) {
         JsonObject root = GSON.fromJson(r, JsonObject.class);
         if (root == null) return;
         for (Hack h : HackManager.all()) {
            if (!root.has(h.getName())) continue;
            JsonObject o = root.getAsJsonObject(h.getName());
            if (o.has("key")) h.setKey(o.get("key").getAsInt());
            if (o.has("settings")) {
               JsonObject settings = o.getAsJsonObject("settings");
               for (Setting s : h.getSettings()) {
                  if (!settings.has(s.getName())) continue;
                  if (s instanceof NumberSetting n) n.set(settings.get(s.getName()).getAsDouble());
                  else if (s instanceof BoolSetting b) b.set(settings.get(s.getName()).getAsBoolean());
               }
            }
            // Açık/kapalı durumu oyuncu dünyaya girince uygulanır (bkz. AnarsiClient).
            if (o.has("enabled") && o.get("enabled").getAsBoolean()) PENDING_ENABLE.add(h);
         }
      } catch (Exception e) {
         AnarsiClient.LOGGER.error("Config okunamadi", e);
      }
   }

   /** Başlangıçta dünya yokken onEnable çağırmamak için ertelenen hileler. */
   public static final java.util.List<Hack> PENDING_ENABLE = new java.util.ArrayList<>();
}
