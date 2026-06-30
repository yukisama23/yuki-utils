package com.yuki.yuki.clickgui;

import com.yuki.yuki.util.UiMath;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;

final class ColorDrafts {
    private final Map<GuiDefinition.ColorSetting, Integer> colors = new IdentityHashMap<>();
    private final Map<GuiDefinition.ColorSetting, float[]> hsv = new IdentityHashMap<>();

    int display(GuiDefinition.ColorSetting setting) {
        Integer color = colors.get(setting);
        return color == null ? setting.get.get() : color;
    }

    String displayHex(GuiDefinition.ColorSetting setting) {
        return String.format("#%06x", display(setting) & 0x00FFFFFF);
    }

    void set(GuiDefinition.ColorSetting setting, int color) {
        colors.put(setting, color);
        setting.previewColor(color);
    }

    void setFromPicker(GuiDefinition.ColorSetting setting, double mouseX, double mouseY, int x, int y, int width) {
        float[] values = displayHsv(setting);
        float saturation = ColorPickerComponent.saturationFromMouse(mouseX, x, y, width);
        float value = ColorPickerComponent.valueFromMouse(mouseY, x, y, width);
        set(setting, values[0], saturation, value);
    }

    void setFromHue(GuiDefinition.ColorSetting setting, double mouseY, int x, int y, int width) {
        float[] values = displayHsv(setting);
        float hue = ColorPickerComponent.hueFromMouse(mouseY, x, y, width);
        set(setting, hue, values[1], values[2]);
    }

    void commit(GuiDefinition.ColorSetting setting) {
        Integer color = colors.remove(setting);
        if (color != null) setting.setColor(color);
    }

    void commitAll() {
        ArrayList<GuiDefinition.ColorSetting> settings = new ArrayList<>(colors.keySet());
        for (GuiDefinition.ColorSetting setting : settings) commit(setting);
    }

    float[] displayHsv(GuiDefinition.ColorSetting setting) {
        float[] values = hsv.get(setting);
        if (values != null) return values;
        values = rgbToHsv(display(setting));
        hsv.put(setting, values);
        return values;
    }

    private void set(GuiDefinition.ColorSetting setting, float hue, float saturation, float value) {
        hue = UiMath.clamp01(hue);
        saturation = UiMath.clamp01(saturation);
        value = UiMath.clamp01(value);
        hsv.put(setting, new float[]{hue, saturation, value});
        set(setting, ColorPickerComponent.hsvToColor(hue, saturation, value));
    }

    private static float[] rgbToHsv(int color) {
        float r = ((color >> 16) & 255) / 255.0f;
        float g = ((color >> 8) & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;
        float hue;
        if (delta == 0.0f) hue = 0.0f;
        else if (max == r) hue = ((g - b) / delta) % 6.0f;
        else if (max == g) hue = (b - r) / delta + 2.0f;
        else hue = (r - g) / delta + 4.0f;
        hue /= 6.0f;
        if (hue < 0.0f) hue += 1.0f;
        float saturation = max == 0.0f ? 0.0f : delta / max;
        return new float[]{hue, saturation, max};
    }
}
