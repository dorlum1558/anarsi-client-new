package com.anarsi.client.gui;

import com.anarsi.client.hack.Hack;
import com.anarsi.client.hack.HackManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/** Sağ üstte, uzunluğa göre sıralı, kategori renkli aktif hile listesi. */
public final class HudOverlay {
   private HudOverlay() {
   }

   public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (HackManager.isHidden() || mc.options.hudHidden || mc.player == null) return;
      TextRenderer font = mc.textRenderer;
      int sw = ctx.getScaledWindowWidth();

      String brand = "Anarşi";
      ctx.fill(4, 4, 8 + font.getWidth(brand) + 4, 17, 0x90000000);
      ctx.fill(4, 4, 6, 17, 0xFFB14CFF);
      ctx.drawTextWithShadow(font, brand, 9, 7, 0xFFB14CFF);

      List<Hack> active = new ArrayList<>();
      for (Hack h : HackManager.all()) if (h.isEnabled()) active.add(h);
      active.sort(Comparator.comparingInt((Hack h) -> font.getWidth(h.getName())).reversed());

      int y = 4;
      for (Hack h : active) {
         int w = font.getWidth(h.getName());
         int x = sw - w - 8;
         ctx.fill(x - 3, y, sw - 2, y + 11, 0x90000000);
         ctx.fill(sw - 2, y, sw, y + 11, h.getCategory().color);
         ctx.drawTextWithShadow(font, h.getName(), x, y + 2, h.getCategory().color);
         y += 11;
      }
   }
}
