package com.yuki.yuki.clickgui;

import com.yuki.yuki.util.GradientTextRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

final class ClickGuiText {
    private ClickGuiText() {}

    static void gradientShadow(DrawContext context, TextRenderer renderer, String text, int x, int y, int color1, int color2) {
        GradientTextRenderer.drawTwoColorShadow(context, renderer, text, x, y, color1, color2);
    }

    static int strippedWidth(GuiTextCache cache, String text) {
        return cache.strippedWidth(text);
    }

    static void shadow(DrawContext context, TextRenderer renderer, GuiTextCache cache, String text, int x, int y, int color) {
        context.drawText(renderer, cache.legacyLower(text), x, y, color, true);
    }

    static void value(DrawContext context, TextRenderer renderer, GuiTextCache cache, String text, int x, int y, int color) {
        context.drawText(renderer, cache.lower(text), x, y, color, true);
    }

    static void hudButton(DrawContext context, TextRenderer renderer, GuiTextCache cache, int x, int y, int width, int height, String label) {
        Ui.drawGradientControl(context, x, y, width, height);
        String shown = fit(cache, label, width - 6);
        int textX = x + Math.max(3, (width - cache.width(shown)) / 2);
        int textY = y + Math.max(3, (height - 8) / 2);
        context.drawText(renderer, cache.lower(shown), textX, textY, Ui.WHITE, true);
    }

    private static String fit(GuiTextCache cache, String text, int maxWidth) {
        String shown = cache.lower(text);
        while (!shown.isEmpty() && cache.width(shown) > maxWidth) shown = shown.substring(0, shown.length() - 1);
        return shown;
    }
}
