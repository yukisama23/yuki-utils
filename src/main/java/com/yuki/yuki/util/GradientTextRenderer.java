package com.yuki.yuki.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public final class GradientTextRenderer {
    private GradientTextRenderer() {}

    public static void drawTwoColorShadow(DrawContext context, TextRenderer textRenderer, String text, int x, int y, int color1, int color2) {
        String visible = normalize(text, true);
        if (visible.isEmpty()) return;
        int totalWidth = Math.max(1, textRenderer.getWidth(visible));
        int cursor = x;
        for (int offset = 0; offset < visible.length(); ) {
            int codePoint = visible.codePointAt(offset);
            String glyph = new String(Character.toChars(codePoint));
            int glyphWidth = textRenderer.getWidth(glyph);
            if (glyphWidth > 0) {
                float progress = ((cursor - x) + glyphWidth * 0.5f) / totalWidth;
                context.drawText(textRenderer, glyph, cursor, y, twoColor(color1, color2, progress), true);
            }
            cursor += glyphWidth;
            offset += Character.charCount(codePoint);
        }
    }

    private static int twoColor(int color1, int color2, float progress) {
        return ColorUtil.lerp(color1, color2, progress);
    }

    public static int strippedWidth(TextRenderer textRenderer, String text) {
        return textRenderer.getWidth(stripLegacyFormatting(text));
    }

    public static String stripLegacyFormatting(String text) {
        if (text == null || text.isEmpty()) return "";
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if ((ch == '&' || ch == '§') && i + 1 < text.length() && isLegacyFormattingCode(text.charAt(i + 1))) {
                i++;
                continue;
            }
            out.append(ch);
        }
        return out.toString();
    }

    private static String normalize(String text, boolean stripLegacy) {
        String visible = text == null ? "" : text;
        if (stripLegacy) visible = stripLegacyFormatting(visible);
        return visible;
    }

    private static boolean isLegacyFormattingCode(char code) {
        return "0123456789abcdefklmnor".indexOf(Character.toLowerCase(code)) >= 0;
    }
}
