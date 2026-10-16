package com.anarsi.client.hack;

import com.anarsi.client.setting.NumberSetting;

/** Velocity: aldığın geri tepmeyi yüzde olarak ölçekler (0 = hiç itilmezsin). */
public class AntiKnockback extends Hack {
   public static AntiKnockback INSTANCE;
   private final NumberSetting horizontal = this.number("Yatay %", 0.0, 0.0, 100.0, 1.0);
   private final NumberSetting vertical = this.number("Dikey %", 0.0, 0.0, 100.0, 1.0);

   public AntiKnockback() {
      super("AntiKnockback", "Vurulunca/patlamada daha az itilirsin (Velocity).", Category.COMBAT);
      INSTANCE = this;
   }

   public static boolean active() {
      return INSTANCE != null && INSTANCE.isEnabled();
   }

   public double h() {
      return this.horizontal.get() / 100.0;
   }

   public double v() {
      return this.vertical.get() / 100.0;
   }
}
