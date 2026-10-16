package com.anarsi.client.mixin;

import com.anarsi.client.hack.Criticals;
import com.anarsi.client.hack.InstaKill;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerInteractionManager.class})
public abstract class ClientPlayerInteractionManagerMixin {
   @Inject(
      method = {"attackEntity"},
      at = {@At("HEAD")}
   )
   private void anarsiclient$attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
      if (Criticals.INSTANCE != null) {
         Criticals.INSTANCE.beforeAttack(player, target);
      }
      if (InstaKill.ACTIVE && InstaKill.INSTANCE != null) {
         InstaKill.INSTANCE.beforeAttack(player, target);
      }
   }
}
