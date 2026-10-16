package com.anarsi.client.hack;

import com.anarsi.client.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;

public class Fly extends Hack {
   private final NumberSetting speed = this.number("Hız", 1.0, 0.1, 5.0, 0.1);

   public Fly() {
      super("Fly", "Uçmanı sağlar. Space: yukarı, Shift: aşağı.", Category.MOVEMENT);
   }

   @Override
   public void onTick() {
      ClientPlayerEntity player = mc.player;
      if (player != null && !player.hasVehicle() && !player.isFallFlying()) {
         double s = this.speed.get();
         float forward = player.input.movementForward;
         float sideways = player.input.movementSideways;
         double yaw = Math.toRadians((double)player.getYaw());
         double sin = Math.sin(yaw);
         double cos = Math.cos(yaw);
         double vx = ((double)sideways * cos - (double)forward * sin) * s;
         double vz = ((double)forward * cos + (double)sideways * sin) * s;
         double vy = 0.0;
         if (mc.options.jumpKey.isPressed()) {
            vy += s;
         }

         if (mc.options.sneakKey.isPressed()) {
            vy -= s;
         }

         player.setVelocity(vx, vy, vz);
      }
   }
}
