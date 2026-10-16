package com.anarsi.client.mixin;

import com.anarsi.client.hack.HackManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Seçenekler ekranının sol alt köşesine sıradan görünen küçük bir "Ses Testi" butonu ekler.
 * Tıklayınca gizli mod aç/kapanır: tüm hileler kapanır, HUD ve menü tuşu devre dışı kalır.
 * Buton metni her iki durumda da aynı kalır, dışarıdan bir şey anlaşılmaz.
 */
@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
   protected OptionsScreenMixin(Text title) {
      super(title);
   }

   @Inject(method = "init", at = @At("TAIL"))
   private void anarsiclient$addHiddenButton(CallbackInfo ci) {
      this.addDrawableChild(ButtonWidget.builder(Text.literal("Ses Testi"), b -> HackManager.toggleHidden())
         .dimensions(6, this.height - 26, 70, 20)
         .build());
   }
}
