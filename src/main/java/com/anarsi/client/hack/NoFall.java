package com.anarsi.client.hack;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;

public class NoFall extends Hack {
   public NoFall() {
      super("NoFall", "Düşme hasarı almazsın.", Category.MOVEMENT);
   }

   @Override
   public void onTick() {
      ClientPlayerEntity player = mc.player;
      if (player != null && !player.isFallFlying() && !player.isOnGround()) {
         if (player.fallDistance > 2.0F) {
            player.networkHandler.sendPacket(new OnGroundOnly(true));
         }
      }
   }
}
