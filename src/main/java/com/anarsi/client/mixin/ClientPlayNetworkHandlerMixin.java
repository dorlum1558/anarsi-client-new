package com.anarsi.client.mixin;

import com.anarsi.client.hack.AntiKnockback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** AntiKnockback: kendi oyuncumuza gelen geri tepme paketlerini ölçekler. */
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
   @Inject(method = "onEntityVelocityUpdate", at = @At("HEAD"), cancellable = true)
   private void anarsiclient$velocity(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (!AntiKnockback.active() || mc.player == null || packet.getEntityId() != mc.player.getId()) return;
      if (!mc.isOnThread()) return; // paket önce ağ thread'inde gelir; ana thread'de işle
      AntiKnockback ak = AntiKnockback.INSTANCE;
      ci.cancel();
      mc.player.setVelocity(packet.getVelocityX() * ak.h(), packet.getVelocityY() * ak.v(), packet.getVelocityZ() * ak.h());
   }

   @Inject(method = "onExplosion", at = @At("TAIL"))
   private void anarsiclient$explosion(ExplosionS2CPacket packet, CallbackInfo ci) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (!AntiKnockback.active() || mc.player == null || !mc.isOnThread()) return;
      AntiKnockback ak = AntiKnockback.INSTANCE;
      // Vanilla oyuncu hızına patlama itmesini ekledi; eklenen kısmı geri al ve ölçekli ekle.
      Vec3d v = mc.player.getVelocity();
      double px = packet.getPlayerVelocityX(), py = packet.getPlayerVelocityY(), pz = packet.getPlayerVelocityZ();
      mc.player.setVelocity(v.x - px + px * ak.h(), v.y - py + py * ak.v(), v.z - pz + pz * ak.h());
   }
}
