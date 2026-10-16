package com.anarsi.client.gui;

import com.anarsi.client.config.AnarsiConfig;
import com.anarsi.client.hack.Category;
import com.anarsi.client.hack.Hack;
import com.anarsi.client.hack.HackManager;
import com.anarsi.client.setting.BoolSetting;
import com.anarsi.client.setting.NumberSetting;
import com.anarsi.client.setting.Setting;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClickGuiScreen extends Screen {
   private static final int PANEL_W = 128;
   private static final int GAP = 8;
   private static final int HEADER_H = 20;
   private static final int ROW_H = 16;
   private static final int SETTING_H = 15;
   private static final int KEY_W = 30;

   private static final int BG = 0xE0121418;
   private static final int ROW = 0xD01A1D23;
   private static final int ROW_HOVER = 0xD0262A33;
   private static final int SETTING_BG = 0xD0101216;
   private static final int TEXT = 0xFFE6E8EB;
   private static final int MUTED = 0xFF8A93A0;
   private static final int ACCENT = 0xFFB14CFF;

   private final List<Hit> hits = new ArrayList<>();
   private final Set<Hack> expanded = new HashSet<>();
   private Hit dragHit;
   /** Tuş ataması bekleyen hile (null = bekleme yok). */
   private Hack listening;

   public ClickGuiScreen() {
      super(Text.literal("Anarsi Client"));
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   @Override
   public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
      ctx.fillGradient(0, 0, this.width, this.height, 0x90000000, 0xB0100818);
   }

   @Override
   public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
      super.render(ctx, mouseX, mouseY, delta);
      this.hits.clear();

      // Başlık
      String title = "ANARŞİ CLIENT";
      int tw = this.textRenderer.getWidth(title);
      ctx.drawTextWithShadow(this.textRenderer, title, (this.width - tw) / 2, 10, ACCENT);
      ctx.fill((this.width - tw) / 2, 21, (this.width + tw) / 2, 22, ACCENT);

      Category[] cats = Category.values();
      int total = cats.length * PANEL_W + (cats.length - 1) * GAP;
      int x = Math.max(6, (this.width - total) / 2);
      String hint = null;

      for (Category cat : cats) {
         int y = 32;
         // Panel başlığı: kategori rengi üst şerit
         ctx.fill(x, y, x + PANEL_W, y + HEADER_H, BG);
         ctx.fill(x, y, x + PANEL_W, y + 2, cat.color);
         ctx.drawTextWithShadow(this.textRenderer, cat.title, x + 7, y + 7, TEXT);
         String count = String.valueOf(HackManager.byCategory(cat).stream().filter(Hack::isEnabled).count());
         ctx.drawTextWithShadow(this.textRenderer, count, x + PANEL_W - 7 - this.textRenderer.getWidth(count), y + 7, MUTED);
         y += HEADER_H;

         for (Hack hack : HackManager.byCategory(cat)) {
            Hit row = new Hit(x, y, PANEL_W - KEY_W, ROW_H, hack, null, false);
            Hit keyBox = new Hit(x + PANEL_W - KEY_W, y, KEY_W, ROW_H, hack, null, true);
            this.hits.add(row);
            this.hits.add(keyBox);
            boolean over = row.contains(mouseX, mouseY) || keyBox.contains(mouseX, mouseY);
            if (over) hint = hack.getDescription();

            ctx.fill(x, y, x + PANEL_W, y + ROW_H, over ? ROW_HOVER : ROW);
            if (hack.isEnabled()) {
               ctx.fill(x, y, x + 2, y + ROW_H, cat.color);
               ctx.fill(x + 2, y, x + PANEL_W, y + ROW_H, (cat.color & 0x00FFFFFF) | 0x22000000);
            }
            ctx.drawTextWithShadow(this.textRenderer, hack.getName(), x + 7, y + 4, hack.isEnabled() ? TEXT : MUTED);
            if (!hack.getSettings().isEmpty()) {
               ctx.drawTextWithShadow(this.textRenderer, this.expanded.contains(hack) ? "▾" : "▸",
                  x + PANEL_W - KEY_W - 9, y + 4, MUTED);
            }
            // Tuş kutusu
            String keyText = this.listening == hack ? "..." : keyName(hack.getKey());
            int kx = x + PANEL_W - KEY_W + 2;
            ctx.fill(kx, y + 2, x + PANEL_W - 3, y + ROW_H - 2, this.listening == hack ? ACCENT : 0xFF0B0C0F);
            int kw = this.textRenderer.getWidth(keyText);
            ctx.drawTextWithShadow(this.textRenderer, keyText, kx + (KEY_W - 5 - kw) / 2, y + 4,
               hack.getKey() < 0 && this.listening != hack ? 0xFF555B66 : TEXT);
            y += ROW_H;

            if (this.expanded.contains(hack)) {
               for (Setting setting : hack.getSettings()) {
                  Hit hit = new Hit(x, y, PANEL_W, SETTING_H, hack, setting, false);
                  this.hits.add(hit);
                  if (hit.contains(mouseX, mouseY) && setting instanceof NumberSetting n && !n.getHint().isEmpty()) {
                     hint = n.getHint();
                  }
                  this.drawSetting(ctx, hit, cat.color);
                  y += SETTING_H;
               }
            }
         }
         ctx.fill(x, y, x + PANEL_W, y + 2, BG);
         x += PANEL_W + GAP;
      }

      // Alt bilgi satırı
      int fy = this.height - 16;
      ctx.fill(0, fy - 4, this.width, this.height, 0xC00B0C0F);
      String footer;
      if (this.listening != null) {
         footer = this.listening.getName() + " için bir tuşa bas  •  ESC/Delete: tuşu kaldır";
      } else if (hint != null) {
         footer = hint;
      } else {
         footer = "Sol tık: aç/kapat  •  Sağ tık: ayarlar  •  Tuş kutusu / orta tık: tuş ata  •  Sağ Shift: kapat";
      }
      ctx.drawTextWithShadow(this.textRenderer, footer, 8, fy, this.listening != null ? ACCENT : MUTED);
      String ver = "v1.1";
      ctx.drawTextWithShadow(this.textRenderer, ver, this.width - 8 - this.textRenderer.getWidth(ver), fy, 0xFF555B66);
   }

   private void drawSetting(DrawContext ctx, Hit hit, int color) {
      ctx.fill(hit.x(), hit.y(), hit.x() + hit.w(), hit.y() + hit.h(), SETTING_BG);
      ctx.fill(hit.x(), hit.y(), hit.x() + 1, hit.y() + hit.h(), color & 0x80FFFFFF);
      if (hit.setting() instanceof NumberSetting number) {
         double ratio = (number.get() - number.getMin()) / (number.getMax() - number.getMin());
         int trackX = hit.x() + 8;
         int trackW = hit.w() - 16;
         int trackY = hit.y() + hit.h() - 3;
         ctx.fill(trackX, trackY, trackX + trackW, trackY + 2, 0xFF2A2E36);
         ctx.fill(trackX, trackY, trackX + (int) (ratio * trackW), trackY + 2, color);
         ctx.drawTextWithShadow(this.textRenderer, number.getName(), hit.x() + 8, hit.y() + 2, MUTED);
         String v = number.display();
         ctx.drawTextWithShadow(this.textRenderer, v, hit.x() + hit.w() - 8 - this.textRenderer.getWidth(v), hit.y() + 2, TEXT);
      } else if (hit.setting() instanceof BoolSetting bool) {
         ctx.drawTextWithShadow(this.textRenderer, bool.getName(), hit.x() + 8, hit.y() + 4, MUTED);
         int bx = hit.x() + hit.w() - 22;
         ctx.fill(bx, hit.y() + 4, bx + 14, hit.y() + 11, bool.get() ? color : 0xFF2A2E36);
         int knob = bool.get() ? bx + 8 : bx + 1;
         ctx.fill(knob, hit.y() + 5, knob + 5, hit.y() + 10, TEXT);
      }
   }

   private static String keyName(int key) {
      if (key < 0) return "-";
      String s = InputUtil.Type.KEYSYM.createFromCode(key).getLocalizedText().getString();
      return s.length() > 4 ? s.substring(0, 4) : s;
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.listening != null) {
         this.listening = null; // tıklama = iptal
         return true;
      }
      for (Hit hit : this.hits) {
         if (!hit.contains(mouseX, mouseY)) continue;
         if (hit.setting() == null) {
            if (hit.keyBox() || button == 2) {
               this.listening = hit.hack();
            } else if (button == 0) {
               hit.hack().toggle();
            } else if (button == 1 && !hit.hack().getSettings().isEmpty() && !this.expanded.remove(hit.hack())) {
               this.expanded.add(hit.hack());
            }
         } else if (hit.setting() instanceof BoolSetting bool) {
            if (button == 0) bool.toggle();
         } else if (hit.setting() instanceof NumberSetting number && button == 0) {
            this.dragHit = hit;
            this.updateSlider(number, hit, mouseX);
         }
         return true;
      }
      return super.mouseClicked(mouseX, mouseY, button);
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (this.dragHit != null && this.dragHit.setting() instanceof NumberSetting number) {
         this.updateSlider(number, this.dragHit, mouseX);
         return true;
      }
      return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      this.dragHit = null;
      return super.mouseReleased(mouseX, mouseY, button);
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.listening != null) {
         if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            this.listening.setKey(-1);
         } else if (keyCode != GLFW.GLFW_KEY_RIGHT_SHIFT) { // menü tuşu atanamaz
            this.listening.setKey(keyCode);
         }
         this.listening = null;
         AnarsiConfig.save();
         return true;
      }
      if (keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
         this.close();
         return true;
      }
      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   @Override
   public void removed() {
      AnarsiConfig.save();
      super.removed();
   }

   private void updateSlider(NumberSetting number, Hit hit, double mouseX) {
      double ratio = (mouseX - (hit.x() + 8)) / (double) (hit.w() - 16);
      ratio = Math.max(0.0, Math.min(1.0, ratio));
      number.set(number.getMin() + ratio * (number.getMax() - number.getMin()));
   }

   private record Hit(int x, int y, int w, int h, Hack hack, Setting setting, boolean keyBox) {
      boolean contains(double mx, double my) {
         return mx >= this.x && mx < this.x + this.w && my >= this.y && my < this.y + this.h;
      }
   }
}
