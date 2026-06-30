package com.yuki.yuki.config;

import java.util.List;
import java.util.Locale;

final class RuntimeHudOptions {
    private static final List<String> ARMOR_STYLE_OPTIONS = List.of("normal", "hotbar");
    private static final List<String> OFFHAND_STYLE_OPTIONS = List.of("none", "normal", "hotbar");
    private static final List<String> POSITION_OPTIONS = List.of("hotbar_left", "hotbar_right", "custom");

    private RuntimeHudOptions() {}

    static List<String> armorStyleOptions() {
        return ARMOR_STYLE_OPTIONS;
    }

    static String armorStyle() {
        return normalizeArmorStyle(Runtime.HUD.armorHudStyle);
    }

    static String normalizeArmorStyle(String style) {
        if (style == null) return "hotbar";
        String normalized = style.trim().toLowerCase(Locale.ROOT);
        return "normal".equals(normalized) ? "normal" : "hotbar";
    }

    static boolean isArmorHotbarStyle() {
        return "hotbar".equals(armorStyle());
    }

    static List<String> offhandStyleOptions() {
        return OFFHAND_STYLE_OPTIONS;
    }

    static String offhandStyle() {
        return normalizeOffhandStyle(Runtime.HUD.armorHudOffhandStyle);
    }

    static String normalizeOffhandStyle(String style) {
        if (style == null) return "hotbar";
        String normalized = style.trim().toLowerCase(Locale.ROOT);
        if ("none".equals(normalized) || "normal".equals(normalized)) return normalized;
        return "hotbar";
    }

    static boolean isOffhandEnabled() {
        return Runtime.HUD.armorHud && !"none".equals(offhandStyle());
    }

    static boolean isOffhandHotbarStyle() {
        return "hotbar".equals(offhandStyle());
    }

    static List<String> positionOptions() {
        return POSITION_OPTIONS;
    }

    static String position() {
        return normalizePosition(Runtime.HUD.armorHudPosition);
    }

    static String normalizePosition(String position) {
        if (position == null) return "hotbar_left";
        String normalized = position.trim().toLowerCase(Locale.ROOT);
        if ("hotbar_right".equals(normalized) || "custom".equals(normalized)) return normalized;
        return "hotbar_left";
    }

    static boolean isCustomPosition() {
        return "custom".equals(position());
    }
}
