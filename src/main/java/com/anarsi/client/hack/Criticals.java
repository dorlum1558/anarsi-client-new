package com.anarsi.client.hack;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/** Her vuruştan önce küçük bir zıplama simüle ederek vuruşu kritik yapar. */
public class Criticals extends Hack {
   public static Criticals INSTANCE;

   public Criticals() {
      super("Criticals", "Her vuruş kritik vuruş olur (Full Kritik).", Category.COMBAT);
      INSTANCE = this;
   }

   public void beforeAttack(PlayerEntity attacker, Entity target) {
      ClientPlayerEntity p = mc.player;
      if (!this.isEnabled() || p == null || attacker != p || !(target instanceof LivingEntity)) return;
      // Kritik için yerde olmak, suda/merdivende olmamak gerekir.
      if (!p.isOnGround() || p.isTouchingWater() || p.isInLava() || p.isClimbing()) return;
      double x = p.getX(), y = p.getY(), z = p.getZ();
      p.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625, z, false));
      p.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false));
   }
}
