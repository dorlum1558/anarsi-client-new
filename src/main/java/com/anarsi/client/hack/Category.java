package com.anarsi.client.hack;

public enum Category {
   COMBAT("Savaş", 0xFFE5484D),
   MOVEMENT("Hareket", 0xFF46A758),
   RENDER("Görsel", 0xFF3E8EED),
   MISC("Diğer", 0xFFF5A524);

   public final String title;
   public final int color;

   Category(String title, int color) {
      this.title = title;
      this.color = color;
   }
}
