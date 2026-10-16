package com.anarsi.client.hack;

import com.anarsi.client.setting.BoolSetting;
import com.anarsi.client.setting.NumberSetting;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;

public class KillAura extends Hack {
   private final NumberSetting range = this.number("Mesafe", 3.9, 2.0, 8.0, 0.1);
   private final BoolSetting players = this.bool("Oyuncular", true);
   private final BoolSetting mobs = this.bool("Moblar", false);

   public KillAura() {
      super("KillAura", "Yakındaki hedeflere otomatik vurur.", Category.COMBAT);
   }

   @Override
   public void onTick() {
      ClientPlayerEntity player = mc.player;
      if (player != null && mc.world != null && mc.interactionManager != null) {
         if (!player.isSpectator() && player.isAlive()) {
            if (!(player.getAttackCooldownProgress(0.5F) < 1.0F)) {
               LivingEntity target = this.findTarget(player);
               if (target != null) {
                  this.faceTarget(player, target);
                  mc.interactionManager.attackEntity(player, target);
                  player.swingHand(Hand.MAIN_HAND);
               }
            }
         }
      }
   }

   private LivingEntity findTarget(ClientPlayerEntity player) {
      LivingEntity best = null;
      double bestDistance = Double.MAX_VALUE;
      double maxDistance = this.range.get();
      Vec3d eye = player.getEyePos();

      for (Entity entity : mc.world.getEntities()) {
         if (entity != player && entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity)entity;
            if (living.isAlive()
               && !(living instanceof ArmorStandEntity)
               && (living instanceof PlayerEntity ? this.players.get() && !living.isSpectator() : this.mobs.get())) {
               double distance = distanceToBox(eye, living.getBoundingBox());
               if (!(distance > maxDistance) && !(distance >= bestDistance)) {
                  best = living;
                  bestDistance = distance;
               }
            }
         }
      }

      return best;
   }

   private static double distanceToBox(Vec3d point, Box box) {
      double dx = Math.max(Math.max(box.minX - point.x, 0.0), point.x - box.maxX);
      double dy = Math.max(Math.max(box.minY - point.y, 0.0), point.y - box.maxY);
      double dz = Math.max(Math.max(box.minZ - point.z, 0.0), point.z - box.maxZ);
      return Math.sqrt(dx * dx + dy * dy + dz * dz);
   }

   private void faceTarget(ClientPlayerEntity player, Entity target) {
      Vec3d eye = player.getEyePos();
      Vec3d to = target.getBoundingBox().getCenter();
      double dx = to.x - eye.x;
      double dy = to.y - eye.y;
      double dz = to.z - eye.z;
      double flat = Math.sqrt(dx * dx + dz * dz);
      float yaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
      float pitch = (float)(-Math.toDegrees(Math.atan2(dy, flat)));
      player.networkHandler.sendPacket(new LookAndOnGround(yaw, pitch, player.isOnGround()));
   }
}
