package com.anarsi.client.hack;

import com.anarsi.client.setting.BoolSetting;
import com.anarsi.client.setting.NumberSetting;
import com.anarsi.client.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public abstract class Hack {
   protected static final MinecraftClient mc = MinecraftClient.getInstance();
   private final String name;
   private final String description;
   private final List<Setting> settings = new ArrayList<>();
   private final Category category;
   private volatile boolean enabled;
   /** GLFW tuş kodu; -1 = atanmamış. */
   private int key = -1;

   protected Hack(String name, String description, Category category) {
      this.name = name;
      this.description = description;
      this.category = category;
   }

   public Category getCategory() {
      return this.category;
   }

   public int getKey() {
      return this.key;
   }

   public void setKey(int key) {
      this.key = key;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public List<Setting> getSettings() {
      return this.settings;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void toggle() {
      this.setEnabled(!this.enabled);
   }

   public void setEnabled(boolean value) {
      if (this.enabled != value) {
         this.enabled = value;
         if (value) {
            this.onEnable();
         } else {
            this.onDisable();
         }
      }
   }

   protected void onEnable() {
   }

   protected void onDisable() {
   }

   public void onTick() {
   }

   protected NumberSetting number(String name, double value, double min, double max, double step) {
      NumberSetting setting = new NumberSetting(name, value, min, max, step);
      this.settings.add(setting);
      return setting;
   }

   protected BoolSetting bool(String name, boolean value) {
      BoolSetting setting = new BoolSetting(name, value);
      this.settings.add(setting);
      return setting;
   }
}
