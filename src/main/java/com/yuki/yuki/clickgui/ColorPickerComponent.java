package com.yuki.yuki.clickgui;

import com.yuki.yuki.util.UiMath;
import net.minecraft.client.gui.DrawContext;

public final class ColorPickerComponent {
    private static final int HUE_X_OFFSET = 12;
    private static final int HUE_Y_OFFSET = 6;
    private static final int HUE_WIDTH = 12;
    private static final int PICKER_X_OFFSET = 18;
    private static final int HUE_INSET = 2;
    private static final int PICKER_INSET = 3;

    private ColorPickerComponent() {}

    public static int size(int width) {
        return Math.max(48, width - 42);
    }

    public static Layout layout(int x, int y, int width) {
        int hueX = x + HUE_X_OFFSET;
        int hueY = y + HUE_Y_OFFSET;
        int pickerSize = size(width);
        int pickerX = hueX + PICKER_X_OFFSET;
        int pickerY = hueY;
        return new Layout(
            x + 6,
            y,
            width - 12,
            pickerSize + 12,
            hueX,
            hueY,
            HUE_WIDTH,
            pickerSize,
            hueX + HUE_INSET,
            hueY + HUE_INSET,
            HUE_WIDTH - HUE_INSET * 2,
            pickerSize - HUE_INSET * 2,
            pickerX,
            pickerY,
            pickerSize,
            pickerSize,
            pickerX + PICKER_INSET,
            pickerY + PICKER_INSET,
            pickerSize - PICKER_INSET * 2,
            pickerSize - PICKER_INSET * 2
        );
    }

    public static void render(DrawContext context, int x, int y, int width, float[] hsv) {
        Layout l = layout(x, y, width);
        Ui.drawColorPickerPanel(context, l.panelX(), l.panelY(), l.panelWidth(), l.panelHeight());
        renderHueBar(context, l.hueInnerX(), l.hueInnerY(), l.hueInnerWidth(), l.hueInnerHeight());
        renderSaturationValuePanel(context, l.pickerInnerX(), l.pickerInnerY(), l.pickerInnerWidth(), l.pickerInnerHeight(), hsv[0]);
        Ui.drawOutline(context, l.hueX(), l.hueY(), l.hueWidth(), l.hueHeight(), Ui.OUTLINE_1, Ui.OUTLINE_2);
        Ui.drawOutline(context, l.pickerX(), l.pickerY(), l.pickerWidth(), l.pickerHeight(), Ui.OUTLINE_1, Ui.OUTLINE_2);

        int hueMark = l.hueY() + Math.round(UiMath.clamp01(hsv[0]) * (l.hueHeight() - 1));
        context.fill(l.hueX() - 2, hueMark - 1, l.hueX() + l.hueWidth() + 2, hueMark + 1, Ui.WHITE);

        int cursorInset = Ui.colorPickerCursorInset();
        int cursorX = l.pickerX() + cursorInset + Math.round(hsv[1] * Math.max(0, l.pickerWidth() - 1 - cursorInset * 2));
        int cursorY = l.pickerY() + cursorInset + Math.round((1.0f - hsv[2]) * Math.max(0, l.pickerHeight() - 1 - cursorInset * 2));
        Ui.drawColorPickerCursor(context, cursorX, cursorY, hsvToColor(hsv[0], hsv[1], hsv[2]));
    }

    public static Hit hit(double mx, double my, int x, int y, int width) {
        Layout l = layout(x, y, width);
        if (UiMath.contains(mx, my, l.pickerX(), l.pickerY(), l.pickerWidth(), l.pickerHeight())) return Hit.PICKER;
        if (UiMath.contains(mx, my, l.hueX(), l.hueY(), l.hueWidth(), l.hueHeight())) return Hit.HUE;
        return Hit.NONE;
    }

    public static float saturationFromMouse(double mx, int x, int y, int width) {
        Layout l = layout(x, y, width);
        int cursorInset = Ui.colorPickerCursorInset();
        return (float) ((mx - (l.pickerX() + cursorInset)) / Math.max(1, l.pickerWidth() - 1 - cursorInset * 2));
    }

    public static float valueFromMouse(double my, int x, int y, int width) {
        Layout l = layout(x, y, width);
        int cursorInset = Ui.colorPickerCursorInset();
        return 1.0f - (float) ((my - (l.pickerY() + cursorInset)) / Math.max(1, l.pickerHeight() - 1 - cursorInset * 2));
    }

    public static float hueFromMouse(double my, int x, int y, int width) {
        Layout l = layout(x, y, width);
        return (float) ((my - l.hueY()) / Math.max(1, l.hueHeight() - 1));
    }

    public static int hsvToColor(float hue, float saturation, float value) {
        hue = hue - (float) Math.floor(hue);
        saturation = Math.max(0.0f, Math.min(1.0f, saturation));
        value = Math.max(0.0f, Math.min(1.0f, value));
        float h = hue * 6.0f;
        int i = (int) Math.floor(h);
        float f = h - i;
        float p = value * (1.0f - saturation);
        float q = value * (1.0f - saturation * f);
        float t = value * (1.0f - saturation * (1.0f - f));
        float r;
        float g;
        float b;
        switch (i % 6) {
            case 0 -> { r = value; g = t; b = p; }
            case 1 -> { r = q; g = value; b = p; }
            case 2 -> { r = p; g = value; b = t; }
            case 3 -> { r = p; g = q; b = value; }
            case 4 -> { r = t; b = value; g = p; }
            default -> { r = value; g = p; b = q; }
        }
        return 0xFF000000 | (Math.round(r * 255.0f) << 16) | (Math.round(g * 255.0f) << 8) | Math.round(b * 255.0f);
    }

    private static void renderHueBar(DrawContext context, int x, int y, int width, int height) {
        int bands = Math.min(height, 72);
        for (int i = 0; i < bands; i++) {
            int y1 = y + i * height / bands;
            int y2 = y + (i + 1) * height / bands;
            float hue = (float) i / Math.max(1, bands - 1);
            context.fill(x, y1, x + width, y2, hsvToColor(hue, 1.0f, 1.0f));
        }
    }

    private static void renderSaturationValuePanel(DrawContext context, int x, int y, int width, int height, float hue) {
        context.fill(x, y, x + width, y + height, hsvToColor(hue, 1.0f, 1.0f));
        int bands = 64;
        for (int i = 0; i < bands; i++) {
            int x1 = x + i * width / bands;
            int x2 = x + (i + 1) * width / bands;
            int alpha = 255 - Math.round(i * 255.0f / Math.max(1, bands - 1));
            context.fill(x1, y, x2, y + height, (alpha << 24) | 0x00ffffff);
        }
        for (int i = 0; i < bands; i++) {
            int y1 = y + i * height / bands;
            int y2 = y + (i + 1) * height / bands;
            int alpha = Math.round(i * 255.0f / Math.max(1, bands - 1));
            context.fill(x, y1, x + width, y2, alpha << 24);
        }
    }

    public record Layout(
        int panelX,
        int panelY,
        int panelWidth,
        int panelHeight,
        int hueX,
        int hueY,
        int hueWidth,
        int hueHeight,
        int hueInnerX,
        int hueInnerY,
        int hueInnerWidth,
        int hueInnerHeight,
        int pickerX,
        int pickerY,
        int pickerWidth,
        int pickerHeight,
        int pickerInnerX,
        int pickerInnerY,
        int pickerInnerWidth,
        int pickerInnerHeight
    ) {}

    public enum Hit { NONE, PICKER, HUE }
}
