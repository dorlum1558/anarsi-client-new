package com.anarsi.client.mixin;

import com.anarsi.client.hack.PlayerESP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MinecraftClient.class})
public abstract class MinecraftClientMixin {
   @Inject(
      method = {"hasOutline"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void anarsiclient$hasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      if (PlayerESP.ACTIVE && entity instanceof PlayerEntity && entity != MinecraftClient.getInstance().player) {
         cir.setReturnValue(true);
      }
   }
}
