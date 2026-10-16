package com.anarsi.client.hack;

import com.anarsi.client.setting.BoolSetting;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class PlayerESP extends Hack {
   public static volatile boolean ACTIVE = false;
   public static PlayerESP INSTANCE;
   private final BoolSetting tracer = this.bool("Tracer", false);

   public PlayerESP() {
      super("PlayerESP", "Oyuncuları duvarların arkasından görürsün.", Category.RENDER);
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

   /** Crosshair'den (bakış yönünün biraz önünden) diğer oyunculara çizgi çizer. */
   public void renderTracers(WorldRenderContext ctx) {
      if (!ACTIVE || !this.tracer.get() || mc.world == null || mc.player == null) return;
      VertexConsumerProvider consumers = ctx.consumers();
      MatrixStack matrices = ctx.matrixStack();
      if (consumers == null || matrices == null) return;

      float delta = ctx.tickCounter().getTickDelta(true);
      Vec3d cam = ctx.camera().getPos();
      Vec3d start = mc.player.getRotationVec(delta).multiply(2.0);
      VertexConsumer lines = consumers.getBuffer(RenderLayer.getLines());
      MatrixStack.Entry entry = matrices.peek();
      Matrix4f m = entry.getPositionMatrix();

      for (Entity e : mc.world.getEntities()) {
         if (!(e instanceof PlayerEntity) || e == mc.player || e.isRemoved()) continue;
         Vec3d end = e.getLerpedPos(delta).add(0.0, e.getHeight() / 2.0, 0.0).subtract(cam);
         Vec3d dir = end.subtract(start).normalize();
         float nx = (float) dir.x, ny = (float) dir.y, nz = (float) dir.z;
         // Mesafeye göre renk: yakın = kırmızı, uzak = yeşil.
         double dist = mc.player.distanceTo(e);
         int color = dist < 15 ? 0xFFFF4040 : (dist < 40 ? 0xFFFFC040 : 0xFF40FF70);
         lines.vertex(m, (float) start.x, (float) start.y, (float) start.z).color(color).normal(entry, nx, ny, nz);
         lines.vertex(m, (float) end.x, (float) end.y, (float) end.z).color(color).normal(entry, nx, ny, nz);
      }
   }
}
