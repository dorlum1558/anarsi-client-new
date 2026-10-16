package com.anarsi.client.setting;

import java.util.Locale;

public class NumberSetting extends Setting {
   private final double min;
   private final double max;
   private final double step;
   private volatile double value;
   private String hint = "";

   public NumberSetting(String name, double value, double min, double max, double step) {
      super(name);
      this.min = min;
      this.max = max;
      this.step = step;
      this.value = this.clamp(value);
   }

   public double get() {
      return this.value;
   }

   public void set(double newValue) {
      double snapped = (double)Math.round((newValue - this.min) / this.step) * this.step + this.min;
      this.value = this.clamp(snapped);
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public NumberSetting hint(String hint) {
      this.hint = hint;
      return this;
   }

   public String getHint() {
      return this.hint;
   }

   public String display() {
      // Adım 0.1'den küçükse (ör. 0.01) iki basamak göster, aksi halde tek/sıfır basamak.
      if (this.step < 0.1) return String.format(Locale.ROOT, "%.2f", this.value);
      if (this.step >= 1.0) return String.format(Locale.ROOT, "%.0f", this.value);
      return String.format(Locale.ROOT, "%.1f", this.value);
   }

   private double clamp(double v) {
      return Math.max(this.min, Math.min(this.max, v));
   }
}
