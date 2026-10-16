package com.anarsi.client.hack;

import com.anarsi.client.AnarsiClient;
import com.anarsi.client.config.AnarsiConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HackManager {
   private static final List<Hack> HACKS = new ArrayList<>();
   /** Gizli mod: açıkken tüm hileler kapalı, HUD gizli, GUI ve tuşlar devre dışı. */
   private static volatile boolean hidden = false;

   private HackManager() {
   }

   public static void init() {
      HACKS.add(new KillAura());
      HACKS.add(new Criticals());
      HACKS.add(new Reach());
      HACKS.add(new InstaKill());
      HACKS.add(new AntiKnockback());
      HACKS.add(new Fly());
      HACKS.add(new Speed());
      HACKS.add(new NoFall());
      HACKS.add(new PlayerESP());
      HACKS.add(new Xray());
      HACKS.add(new FullBright());
      AnarsiConfig.load();
   }

   public static List<Hack> all() {
      return Collections.unmodifiableList(HACKS);
   }

   public static List<Hack> byCategory(Category category) {
      List<Hack> out = new ArrayList<>();
      for (Hack h : HACKS) {
         if (h.getCategory() == category) out.add(h);
      }
      return out;
   }

   public static boolean isHidden() {
      return hidden;
   }

   /** Gizli modu aç/kapat. Açılırken tüm hileler kapatılır (sezdirmeden). */
   public static void toggleHidden() {
      hidden = !hidden;
      if (hidden) {
         for (Hack h : HACKS) h.setEnabled(false);
      }
      AnarsiClient.LOGGER.debug("hidden={}", hidden);
   }

   public static void tick() {
      if (hidden) return;
      for (Hack hack : HACKS) {
         if (hack.isEnabled()) {
            try {
               hack.onTick();
            } catch (Throwable t) {
               AnarsiClient.LOGGER.error("{} hata verdi ve kapatildi", hack.getName(), t);
               hack.setEnabled(false);
            }
         }
      }
   }
}
