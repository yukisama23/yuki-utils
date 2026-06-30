package com.yuki.yuki.clickgui;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.render.CustomTooltipRoundRectRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.Locale;

public final class Ui {
    public static final int WHITE = 0xffffffff;
    public static final int CATEGORY_BACKGROUND = 0x6E000000;
    public static final int CONTROL_BACKGROUND = 0x8C05070A;
    public static final int TEXT_DISABLED = 0xFFC0C0C0;
    public static final int TEXT_CATEGORY = 0xFFDCDCDC;
    public static final int TEXT_SETTING = 0xFFAAAAAA;
    public static final int TEXT_SETTING_VALUE = 0xFF9BE7FF;
    public static final int OUTLINE_1 = 0xFF516395;
    public static final int OUTLINE_2 = 0xFF614385;
    private static final int PANEL_RADIUS = 14;

    private Ui() {}

    public static int blue() {
        return Runtime.getClickGuiColor();
    }

    public static int blue2() {
        return Runtime.getClickGuiColor2();
    }

    private static int panelRadius(int width) {
        return Math.min(PANEL_RADIUS, Math.max(0, width / 2));
    }

    public static void drawPanel(DrawContext context, int x, int y, int width, int height, boolean active) {
        if (width <= 0 || height <= 0) return;
        int radius = panelRadius(width);
        int color1 = active ? blue() : OUTLINE_1;
        int color2 = active ? blue2() : OUTLINE_2;
        CustomTooltipRoundRectRenderer.drawFill(context, x, y, width, height, radius, CATEGORY_BACKGROUND);
        CustomTooltipRoundRectRenderer.drawBorder(context, x, y, width, height, radius, 2.5f, color1, color2);
    }

    public static void drawGradientControl(DrawContext context, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) return;
        int radius = panelRadius(width);
        CustomTooltipRoundRectRenderer.drawFill(context, x, y, width, height, radius, CONTROL_BACKGROUND);
        CustomTooltipRoundRectRenderer.drawBorder(context, x, y, width, height, radius, 1.5f, blue(), blue2());
    }

    public static void drawSolidControl(DrawContext context, int x, int y, int width, int height, boolean hover) {
        if (width <= 0 || height <= 0) return;
        int radius = panelRadius(width);
        CustomTooltipRoundRectRenderer.drawFill(context, x, y, width, height, radius, hover ? 0xFF101318 : 0xFF05070A);
        CustomTooltipRoundRectRenderer.drawBorder(context, x, y, width, height, radius, 1.5f, hover ? blue() : OUTLINE_1, hover ? blue2() : OUTLINE_2);
    }

    public static void drawSliderTrack(DrawContext context, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) return;
        int radius = Math.min(Math.max(1, height / 2), Math.max(1, width / 2));
        CustomTooltipRoundRectRenderer.drawFill(context, x, y, width, height, radius, CONTROL_BACKGROUND);
        CustomTooltipRoundRectRenderer.drawBorder(context, x, y, width, height, radius, 1.0f, OUTLINE_1, OUTLINE_2);
    }

    public static void drawSliderProgressOutline(DrawContext context, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) return;
        int radius = Math.min(Math.max(1, height / 2), Math.max(1, width / 2));
        CustomTooltipRoundRectRenderer.drawBorder(context, x, y, width, height, radius, 1.0f, blue(), blue2());
    }

    public static void drawSliderKnob(DrawContext context, int x, int y, int width, int height, boolean hover) {
        drawSolidControl(context, x, y, width, height, hover);
    }

    public static void drawOutline(DrawContext context, int x, int y, int width, int height, int color1, int color2) {
        if (width <= 0 || height <= 0) return;
        int radius = panelRadius(width);
        CustomTooltipRoundRectRenderer.drawBorder(context, x, y, width, height, radius, 1.5f, color1, color2);
    }

    public static void drawColorPickerPanel(DrawContext context, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) return;
        int radius = panelRadius(width);
        CustomTooltipRoundRectRenderer.drawFill(context, x, y, width, height, radius, CONTROL_BACKGROUND);
        CustomTooltipRoundRectRenderer.drawBorder(context, x, y, width, height, radius, 2.5f, blue(), blue2());
    }

    public static void drawColorSwatch(DrawContext context, int x, int y, int size, int color, boolean hover) {
        if (size <= 0) return;
        int radius = Math.max(1, size / 5);
        CustomTooltipRoundRectRenderer.drawFill(context, x, y, size, size, radius, color);
        CustomTooltipRoundRectRenderer.drawBorder(context, x, y, size, size, radius, 1.5f, hover ? blue() : OUTLINE_1, hover ? blue2() : OUTLINE_2);
    }

    public static int colorPickerCursorSize() {
        return 7;
    }

    public static int colorPickerCursorInset() {
        return colorPickerCursorSize() / 2 + 1;
    }

    public static void drawColorPickerCursor(DrawContext context, int centerX, int centerY, int color) {
        int size = colorPickerCursorSize();
        int x = centerX - size / 2;
        int y = centerY - size / 2;
        int radius = Math.max(1, size / 4);
        CustomTooltipRoundRectRenderer.drawFill(context, x, y, size, size, radius, color);
        CustomTooltipRoundRectRenderer.drawBorder(context, x, y, size, size, radius, 1.8f, blue(), blue2());
        CustomTooltipRoundRectRenderer.drawBorder(context, x - 1, y - 1, size + 2, size + 2, radius + 1, 1.0f, OUTLINE_1, OUTLINE_2);
    }

    public static String subSettingText(String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT);
    }
}
