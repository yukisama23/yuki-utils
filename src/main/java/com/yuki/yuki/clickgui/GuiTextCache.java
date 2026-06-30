package com.yuki.yuki.clickgui;

import com.yuki.yuki.util.GradientTextRenderer;
import com.yuki.yuki.util.LegacyText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class GuiTextCache {
    private TextRenderer renderer;
    private final Map<String, String> lowerCache = new HashMap<>();
    private final Map<String, Integer> widthCache = new HashMap<>();
    private final Map<String, Integer> strippedWidthCache = new HashMap<>();
    private final Map<String, Text> legacyCache = new HashMap<>();

    public void use(TextRenderer renderer) {
        if (this.renderer == renderer) return;
        this.renderer = renderer;
        widthCache.clear();
        strippedWidthCache.clear();
    }

    public String lower(String text) {
        String value = text == null ? "" : text;
        return lowerCache.computeIfAbsent(value, key -> key.toLowerCase(Locale.ROOT));
    }

    public Text legacyLower(String text) {
        String value = lower(text);
        return legacyCache.computeIfAbsent(value, LegacyText::parse);
    }

    public int width(String text) {
        String value = lower(text);
        return widthCache.computeIfAbsent(value, key -> renderer.getWidth(key));
    }

    public int strippedWidth(String text) {
        String value = text == null ? "" : text;
        return strippedWidthCache.computeIfAbsent(value, key -> GradientTextRenderer.strippedWidth(renderer, key));
    }
}
