package com.anarsi.client.hack;

import com.anarsi.client.setting.NumberSetting;
import net.minecraft.client.network.ClientPlayerEntity;

public class Speed extends Hack {
   /** Vanilla koşma hızına yakın, tespit riski düşük önerilen değer. */
   public static final double LEGIT = 0.28;
   private final NumberSetting speed = this.number("Hız", LEGIT, 0.1, 1.0, 0.01).hint("Önerilen legit: 0.28");

   public Speed() {
      super("Speed", "Yürüme hızını artırır. Önerilen legit hız: 0.28", Category.MOVEMENT);
   }

   @Override
   public void onTick() {
      ClientPlayerEntity p = mc.player;
      if (p == null || p.isFallFlying() || p.hasVehicle() || p.isTouchingWater() || !p.isOnGround()) return;
      float forward = p.input.movementForward;
      float sideways = p.input.movementSideways;
      if (forward == 0.0F && sideways == 0.0F) return;
      double s = this.speed.get();
      double yaw = Math.toRadians(p.getYaw());
      double sin = Math.sin(yaw), cos = Math.cos(yaw);
      double len = Math.sqrt(forward * forward + sideways * sideways);
      double f = forward / len, st = sideways / len;
      double vx = (st * cos - f * sin) * s;
      double vz = (f * cos + st * sin) * s;
      p.setVelocity(vx, p.getVelocity().y, vz);
   }
}
