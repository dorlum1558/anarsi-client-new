package com.anarsi.client.hack;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

/** Gece görüşü efektiyle her yeri aydınlatır; kapatınca efekt kaldırılır. */
public class FullBright extends Hack {
   public FullBright() {
      super("FullBright", "Karanlıkta her şeyi aydınlık görürsün.", Category.RENDER);
   }

   @Override
   public void onTick() {
      if (mc.player != null) {
         // Kısa süreli ve partikülsüz; her tick yenilenir, ekranda yanıp sönme olmaz.
         mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 420, 0, false, false, false));
      }
   }

   @Override
   protected void onDisable() {
      if (mc.player != null) {
         mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
      }
   }
}
