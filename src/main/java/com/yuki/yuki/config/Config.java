package com.yuki.yuki.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.Map;

public class Config {
    public Colors colors = new Colors();
    public Hud hud = new Hud();
    public Visual visual = new Visual();
    public Player player = new Player();
    public Misc misc = new Misc();

    public static Config fromJson(JsonObject object, Gson gson) {
        Config cfg = object == null ? new Config() : gson.fromJson(object, Config.class);
        if (cfg == null) cfg = new Config();
        cfg.ensureSections();
        cfg.applyLegacy(object);
        return cfg;
    }

    public static String[] pathFor(String key) {
        String joined = LegacyConfigPaths.pathFor(key);
        return joined == null ? null : joined.split("\\.");
    }

    public static void removeLegacyKeys(JsonObject object) {
        if (object == null) return;
        for (String key : LegacyConfigPaths.keys()) object.remove(key);
        object.remove("scoreboardCustomPosition");
        object.remove("scoreboardX");
        object.remove("scoreboardY");
    }

    private void ensureSections() {
        if (colors == null) colors = new Colors();
        if (hud == null) hud = new Hud();
        if (visual == null) visual = new Visual();
        if (player == null) player = new Player();
        if (misc == null) misc = new Misc();
    }

    private void applyLegacy(JsonObject object) {
        if (object == null) return;
        for (Map.Entry<String, String> entry : LegacyConfigPaths.entries()) {
            JsonElement value = object.get(entry.getKey());
            if (value == null || !value.isJsonPrimitive()) continue;
            setLegacy(entry.getValue(), value);
        }
    }

    private void setLegacy(String path, JsonElement value) {
        String[] parts = path.split("\\.");
        if (parts.length != 2) return;

        Object section = section(parts[0]);
        if (section == null) return;

        try {
            Field field = section.getClass().getField(parts[1]);
            setField(section, field, value);
        } catch (ReflectiveOperationException | IllegalStateException | NumberFormatException ignored) {
        }
    }

    private Object section(String name) {
        return switch (name) {
            case "colors" -> colors;
            case "hud" -> hud;
            case "visual" -> visual;
            case "player" -> player;
            case "misc" -> misc;
            default -> null;
        };
    }

    private static void setField(Object owner, Field field, JsonElement value) throws IllegalAccessException {
        Class<?> type = field.getType();
        if (type == boolean.class) {
            field.setBoolean(owner, value.getAsBoolean());
        } else if (type == int.class) {
            field.setInt(owner, value.getAsInt());
        } else if (type == float.class) {
            field.setFloat(owner, value.getAsFloat());
        } else if (type == String.class) {
            field.set(owner, value.getAsString());
        }
    }

    public static class Colors {
        public int clickGuiColor = Runtime.DEFAULT_CLICK_GUI_COLOR;
        public int clickGuiColor2 = Runtime.DEFAULT_CLICK_GUI_COLOR_2;
        public int customTooltipBorderColor1 = Runtime.DEFAULT_GRADIENT_COLOR_1;
        public int customTooltipBorderColor2 = Runtime.DEFAULT_GRADIENT_COLOR_2;
        public int customTooltipBackgroundColor = Runtime.DEFAULT_CUSTOM_TOOLTIP_BACKGROUND_COLOR;
        public int blockOutlineColor1 = Runtime.DEFAULT_GRADIENT_COLOR_1;
        public int blockOutlineColor2 = Runtime.DEFAULT_GRADIENT_COLOR_2;
        public int blockOutlineFilledColor = Runtime.DEFAULT_BLOCK_OUTLINE_FILLED_COLOR;
    }

    public static class Hud {
        public boolean totemTweaks = false;
        public boolean totemTweaksDisableAnimations = false;
        public boolean totemTweaksTotemCount = false;
        public boolean totemTweaksTotemAlert = false;
        public int totemTweaksHudX = -1;
        public int totemTweaksHudY = -1;
        public float totemTweaksHudScale = 1.0f;
        public int totemTweaksAlertHudX = -1;
        public int totemTweaksAlertHudY = -1;
        public float totemTweaksAlertHudScale = 1.0f;
        public boolean reachDisplayHud = false;
        public int reachDisplayHudX = -1;
        public int reachDisplayHudY = -1;
        public float reachDisplayHudScale = 1.0f;
        public boolean armorHud = false;
        public String armorHudStyle = "hotbar";
        public String armorHudOffhandStyle = "hotbar";
        public String armorHudPosition = "hotbar_left";
        public int armorHudX = -1;
        public int armorHudY = -1;
        public float armorHudScale = 1.0f;
        public int armorHudOffhandX = -1;
        public int armorHudOffhandY = -1;
        public float armorHudOffhandScale = 1.0f;
        public boolean effectHud = false;
        public int effectHudX = -1;
        public int effectHudY = -1;
        public float effectHudScale = 1.0f;
        public boolean shulkerTooltips = false;
        public boolean shulkerTooltipsShiftOnly = false;
    }

    public static class Visual {
        public boolean customTooltip = true;
        public boolean customTooltipBorder = true;
        public float customTooltipBorderWidth = 2.0f;
        public int customTooltipRoundness = 12;
        public float customTooltipBackgroundAlpha = 0.85f;
        public boolean fullbright = true;
        public boolean blockOutline = false;
        public float blockOutlineWidth = 2.0f;
        public boolean blockOutlinePhase = false;
        public boolean blockOutlineFilled = false;
        public float blockOutlineFilledAlpha = 0.2f;
        public boolean hideDefenseIcon = false;
        public boolean itemAnimations = false;
        public float itemAnimationsScale = 0.0f;
        public float itemAnimationsSwingSpeed = 0.0f;
        public boolean itemAnimationsIgnoreEffects = false;
        public boolean nameTagTweaks = false;
        public boolean nameTagTweaksShowOwn = false;
        public boolean nameTagTweaksDisableBackground = false;
        public boolean nameTagTweaksShadowedText = false;
        public boolean scoreboard = false;
        public boolean scoreboardShadowedText = false;
        public boolean scoreboardDisableBackground = false;
        public boolean disableHeartsShake = false;
        public boolean effects = false;
        public boolean disableGlowEffect = false;
        public boolean armorHider = false;
        public boolean armorHiderSkull = false;
        public boolean armorHiderHelmet = false;
        public boolean armorHiderChestplate = false;
        public boolean armorHiderElytra = false;
        public boolean armorHiderLeggings = false;
        public boolean armorHiderBoots = false;
    }

    public static class Player {
        public boolean autoSprint = true;
    }

    public static class Misc {
        public boolean chatTweaks = false;
        public boolean chatTweaksCopy = false;
        public boolean animationFix = false;
        public boolean chatNotifications = false;
    }
}
