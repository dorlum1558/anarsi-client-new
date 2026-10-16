package com.anarsi.client.setting;

public class BoolSetting extends Setting {
   private volatile boolean value;

   public BoolSetting(String name, boolean value) {
      super(name);
      this.value = value;
   }

   public boolean get() {
      return this.value;
   }

   public void set(boolean value) {
      this.value = value;
   }

   public void toggle() {
      this.value = !this.value;
   }
}
