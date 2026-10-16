package com.anarsi.client;

import com.anarsi.client.config.AnarsiConfig;
import com.anarsi.client.gui.ClickGuiScreen;
import com.anarsi.client.gui.HudOverlay;
import com.anarsi.client.hack.Hack;
import com.anarsi.client.hack.HackManager;
import com.anarsi.client.hack.PlayerESP;
import java.util.HashSet;
import java.util.Set;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnarsiClient implements ClientModInitializer {
   public static final String MOD_ID = "anarsiclient";
   public static final Logger LOGGER = LoggerFactory.getLogger("anarsiclient");
   private static KeyBinding guiKey;
   /** Bir önceki tick'te basılı olan hile tuşları (kenar algılama için). */
   private static final Set<Integer> HELD = new HashSet<>();
   private static int saveCooldown = 0;

   @Override
   public void onInitializeClient() {
      HackManager.init();
      guiKey = KeyBindingHelper.registerKeyBinding(
         new KeyBinding("key.anarsiclient.gui", InputUtil.Type.KEYSYM, 344, "category.anarsiclient"));

      ClientTickEvents.END_CLIENT_TICK.register(client -> {
         while (guiKey.wasPressed()) {
            // Gizli moddayken GUI tuşu hiçbir şey yapmaz.
            if (client.currentScreen == null && !HackManager.isHidden()) {
               client.setScreen(new ClickGuiScreen());
            }
         }

         if (client.player != null && client.world != null) {
            if (!AnarsiConfig.PENDING_ENABLE.isEmpty() && !HackManager.isHidden()) {
               for (Hack h : AnarsiConfig.PENDING_ENABLE) h.setEnabled(true);
               AnarsiConfig.PENDING_ENABLE.clear();
            }
            handleHackKeys(client);
            HackManager.tick();
         }

         // Ayarlar değiştiyse ara ara diske yaz (her 10 sn).
         if (++saveCooldown >= 200) {
            saveCooldown = 0;
            AnarsiConfig.save();
         }
      });

      WorldRenderEvents.AFTER_ENTITIES.register(ctx -> {
         if (PlayerESP.INSTANCE != null && !HackManager.isHidden()) PlayerESP.INSTANCE.renderTracers(ctx);
      });
      HudRenderCallback.EVENT.register(HudOverlay::render);
      ClientLifecycleEvents.CLIENT_STOPPING.register(c -> AnarsiConfig.save());
      LOGGER.info("Anarsi Client yuklendi. ClickGUI tusu: Right Shift");
   }

   /** Ekran açık değilken, hileye atanmış tuşa basılınca (tek sefer) hileyi aç/kapat. */
   private static void handleHackKeys(MinecraftClient client) {
      if (client.currentScreen != null || HackManager.isHidden()) {
         HELD.clear();
         return;
      }
      long window = client.getWindow().getHandle();
      for (Hack h : HackManager.all()) {
         int key = h.getKey();
         if (key < 0) continue;
         boolean down = InputUtil.isKeyPressed(window, key);
         if (down && HELD.add(key)) {
            h.toggle();
         } else if (!down) {
            HELD.remove(key);
         }
      }
   }
}
