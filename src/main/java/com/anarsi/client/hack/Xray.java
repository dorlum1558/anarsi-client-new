package com.anarsi.client.hack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;

public class Xray extends Hack {
   public static volatile boolean ACTIVE = false;
   private static final Map<Block, Boolean> CACHE = new ConcurrentHashMap<>();

   public Xray() {
      super("Xray", "Sadece maden, sandık ve spawner görünür. Sodium ile çalışmaz.", Category.RENDER);
   }

   public static boolean isVisibleBlock(Block block) {
      return CACHE.computeIfAbsent(block, Xray::compute);
   }

   private static boolean compute(Block block) {
      String path = Registries.BLOCK.getId(block).getPath();
      return path.endsWith("_ore")
         || path.equals("ancient_debris")
         || path.equals("spawner")
         || path.equals("trial_spawner")
         || path.equals("chest")
         || path.equals("trapped_chest")
         || path.equals("ender_chest")
         || path.equals("lava");
   }

   @Override
   protected void onEnable() {
      ACTIVE = true;
      mc.chunkCullingEnabled = false;
      this.reloadWorld();
   }

   @Override
   protected void onDisable() {
      ACTIVE = false;
      mc.chunkCullingEnabled = true;
      this.reloadWorld();
   }

   private void reloadWorld() {
      if (mc.worldRenderer != null) {
         mc.worldRenderer.reload();
      }
   }
}
