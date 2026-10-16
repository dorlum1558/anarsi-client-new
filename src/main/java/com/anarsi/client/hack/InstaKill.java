package com.anarsi.client.hack;

import com.anarsi.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;

public class InstaKill extends Hack {
   public static volatile boolean ACTIVE = false;
   public static InstaKill INSTANCE;
   private final NumberSetting height = this.number("Yükseklik", 19.0, 4.0, 22.0, 1.0);

   public InstaKill() {
      super("InstaKill", "Elinde gürz (mace) varken tek vuruşta ağır hasar. Üstünde boşluk gerekir.", Category.COMBAT);
      INSTANCE = this;
   }

   @Override
   protected void onEnable() {
      ACTIVE = true;
   }

   @Override
   protected void onDisable() {
      ACTIVE = false;
   }

   public void beforeAttack(PlayerEntity attacker, Entity target) {
      ClientPlayerEntity player = mc.player;
      if (player != null && attacker == player && mc.world != null) {
         if (target instanceof LivingEntity) {
            if (player.getMainHandStack().isOf(Items.MACE)) {
               if (!player.isFallFlying() && !player.hasVehicle()) {
                  double h = this.height.get();

                  while (h >= 2.0 && !mc.world.isSpaceEmpty(player, player.getBoundingBox().stretch(0.0, h, 0.0))) {
                     h--;
                  }

                  if (!(h < 2.0)) {
                     int needed = (int)Math.ceil(h * h / 100.0);
                     int fillers = Math.max(0, needed - 1);
                     double x = player.getX();
                     double y = player.getY();
                     double z = player.getZ();

                     for (int i = 0; i < fillers; i++) {
                        player.networkHandler.sendPacket(new OnGroundOnly(false));
                     }

                     player.networkHandler.sendPacket(new PositionAndOnGround(x, y + h, z, false));
                     player.networkHandler.sendPacket(new PositionAndOnGround(x, y, z, false));
                  }
               }
            }
         }
      }
   }
}
