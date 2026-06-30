package com.yuki.yuki.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class LegacyConfigPaths {
    private static final Map<String, String> PATHS = createPaths();

    private LegacyConfigPaths() {}

    static String pathFor(String key) {
        return PATHS.get(key);
    }

    static Set<String> keys() {
        return PATHS.keySet();
    }

    static Set<Map.Entry<String, String>> entries() {
        return PATHS.entrySet();
    }

    private static Map<String, String> createPaths() {
        LinkedHashMap<String, String> paths = new LinkedHashMap<>();
        add(paths, "colors",
            "clickGuiColor", "clickGuiColor2",
            "customTooltipBorderColor1", "customTooltipBorderColor2", "customTooltipBackgroundColor",
            "blockOutlineColor1", "blockOutlineColor2", "blockOutlineFilledColor"
        );
        add(paths, "hud",
            "totemTweaks", "totemTweaksDisableAnimations", "totemTweaksTotemCount", "totemTweaksTotemAlert",
            "totemTweaksHudX", "totemTweaksHudY", "totemTweaksHudScale",
            "totemTweaksAlertHudX", "totemTweaksAlertHudY", "totemTweaksAlertHudScale",
            "reachDisplayHud", "reachDisplayHudX", "reachDisplayHudY", "reachDisplayHudScale",
            "armorHud", "armorHudStyle", "armorHudOffhandStyle", "armorHudPosition",
            "armorHudX", "armorHudY", "armorHudScale", "armorHudOffhandX", "armorHudOffhandY", "armorHudOffhandScale",
            "effectHud", "effectHudX", "effectHudY", "effectHudScale",
            "shulkerTooltips", "shulkerTooltipsShiftOnly"
        );
        add(paths, "visual",
            "customTooltip", "customTooltipBorder", "customTooltipBorderWidth", "customTooltipRoundness", "customTooltipBackgroundAlpha",
            "fullbright", "blockOutline", "blockOutlineWidth", "blockOutlinePhase", "blockOutlineFilled", "blockOutlineFilledAlpha",
            "hideDefenseIcon", "itemAnimations", "itemAnimationsScale", "itemAnimationsSwingSpeed", "itemAnimationsIgnoreEffects",
            "nameTagTweaks", "nameTagTweaksShowOwn", "nameTagTweaksDisableBackground", "nameTagTweaksShadowedText",
            "scoreboard", "scoreboardShadowedText", "scoreboardDisableBackground",
            "disableHeartsShake", "effects", "disableGlowEffect",
            "armorHider", "armorHiderSkull", "armorHiderHelmet", "armorHiderChestplate", "armorHiderElytra", "armorHiderLeggings", "armorHiderBoots"
        );
        add(paths, "player", "autoSprint");
        add(paths, "misc", "chatTweaks", "chatTweaksCopy", "animationFix", "chatNotifications");
        return Map.copyOf(paths);
    }

    private static void add(Map<String, String> paths, String section, String... keys) {
        for (String key : keys) paths.put(key, section + "." + key);
    }
}
