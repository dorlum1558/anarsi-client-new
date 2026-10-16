package com.anarsi.client.hack;

import com.anarsi.client.setting.NumberSetting;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.client.network.ClientPlayerEntity;

public class Reach extends Hack {
   private static final double DEFAULT_ENTITY_RANGE = 3.0;
   private static final double DEFAULT_BLOCK_RANGE = 4.5;
   private final NumberSetting distance = this.number("Mesafe", 6.0, 3.0, 12.0, 0.5);

   public Reach() {
      super("Reach", "Vurma ve blok kırma/koyma mesafeni uzatır.", Category.COMBAT);
   }

   @Override
   public void onTick() {
      double entityRange = this.distance.get();
      this.apply(entityRange, Math.max(4.5, entityRange));
   }

   @Override
   protected void onDisable() {
      this.apply(3.0, 4.5);
   }

   private void apply(double entityRange, double blockRange) {
      ClientPlayerEntity player = mc.player;
      if (player != null) {
         EntityAttributeInstance entity = player.getAttributeInstance(EntityAttributes.PLAYER_ENTITY_INTERACTION_RANGE);
         if (entity != null) {
            entity.setBaseValue(entityRange);
         }

         EntityAttributeInstance block = player.getAttributeInstance(EntityAttributes.PLAYER_BLOCK_INTERACTION_RANGE);
         if (block != null) {
            block.setBaseValue(blockRange);
         }
      }
   }
}
