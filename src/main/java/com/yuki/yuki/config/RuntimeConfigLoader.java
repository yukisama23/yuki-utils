package com.yuki.yuki.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.yuki.yuki.Yuki;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

final class RuntimeConfigLoader {
    private static final Gson GSON = new GsonBuilder().create();

    private RuntimeConfigLoader() {}

    static void load(Path path) {
        try {
            if (!Files.exists(path)) return;
            JsonObject object = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            apply(Config.fromJson(object, GSON), object);
        } catch (IOException | RuntimeException exception) {
            Yuki.LOGGER.warn("Failed to load runtime config", exception);
        }
    }

    private static void apply(Config cfg, JsonObject object) {
        applyColors(cfg.colors);
        applyHud(cfg.hud);
        applyVisual(cfg.visual, cfg.colors);
        applyMisc(cfg.misc);
        applyPlayer(cfg.player);
        loadModuleKeyBinds(object);
    }

    private static void applyColors(Config.Colors colors) {
        Runtime.COLORS.clickGuiColor = Runtime.normalizeColor(colors.clickGuiColor);
        Runtime.COLORS.clickGuiColor2 = Runtime.normalizeColor(colors.clickGuiColor2);
    }

    private static void applyHud(Config.Hud hud) {
        Runtime.HUD.totemTweaks = hud.totemTweaks;
        Runtime.HUD.totemTweaksDisableAnimations = hud.totemTweaksDisableAnimations;
        Runtime.HUD.totemTweaksTotemCount = hud.totemTweaksTotemCount;
        Runtime.HUD.totemTweaksTotemAlert = hud.totemTweaksTotemAlert;
        Runtime.HUD.totemTweaksHudX = hud.totemTweaksHudX;
        Runtime.HUD.totemTweaksHudY = hud.totemTweaksHudY;
        Runtime.HUD.totemTweaksHudScale = Runtime.clampHudScale(hud.totemTweaksHudScale);
        Runtime.HUD.totemTweaksAlertHudX = hud.totemTweaksAlertHudX;
        Runtime.HUD.totemTweaksAlertHudY = hud.totemTweaksAlertHudY;
        Runtime.HUD.totemTweaksAlertHudScale = Runtime.clampHudScale(hud.totemTweaksAlertHudScale);

        Runtime.HUD.reachDisplayHud = hud.reachDisplayHud;
        Runtime.HUD.reachDisplayHudX = hud.reachDisplayHudX;
        Runtime.HUD.reachDisplayHudY = hud.reachDisplayHudY;
        Runtime.HUD.reachDisplayHudScale = Runtime.clampHudScale(hud.reachDisplayHudScale);

        Runtime.HUD.armorHud = hud.armorHud;
        Runtime.HUD.armorHudStyle = Runtime.normalizeArmorHudStyle(hud.armorHudStyle);
        Runtime.HUD.armorHudOffhandStyle = Runtime.normalizeArmorHudOffhandStyle(hud.armorHudOffhandStyle);
        Runtime.HUD.armorHudPosition = Runtime.normalizeArmorHudPosition(hud.armorHudPosition);
        Runtime.HUD.armorHudX = hud.armorHudX;
        Runtime.HUD.armorHudY = hud.armorHudY;
        Runtime.HUD.armorHudScale = Runtime.clampHudScale(hud.armorHudScale);
        Runtime.HUD.armorHudOffhandX = hud.armorHudOffhandX;
        Runtime.HUD.armorHudOffhandY = hud.armorHudOffhandY;
        Runtime.HUD.armorHudOffhandScale = Runtime.clampHudScale(hud.armorHudOffhandScale);

        Runtime.HUD.effectHud = hud.effectHud;
        Runtime.HUD.effectHudX = hud.effectHudX;
        Runtime.HUD.effectHudY = hud.effectHudY;
        Runtime.HUD.effectHudScale = Runtime.clampHudScale(hud.effectHudScale);

        Runtime.HUD.shulkerTooltips = hud.shulkerTooltips;
        Runtime.HUD.shulkerTooltipsShiftOnly = hud.shulkerTooltipsShiftOnly;
    }

    private static void applyVisual(Config.Visual visual, Config.Colors colors) {
        Runtime.VISUAL.customTooltip = visual.customTooltip;
        Runtime.VISUAL.customTooltipBorder = visual.customTooltipBorder;
        Runtime.VISUAL.customTooltipBorderWidth = Runtime.clampCustomTooltipBorderWidth(visual.customTooltipBorderWidth);
        Runtime.VISUAL.customTooltipBorderColor1 = Runtime.normalizeColor(colors.customTooltipBorderColor1);
        Runtime.VISUAL.customTooltipBorderColor2 = Runtime.normalizeColor(colors.customTooltipBorderColor2);
        Runtime.VISUAL.customTooltipRoundness = Runtime.clampCustomTooltipRoundness(visual.customTooltipRoundness);
        Runtime.VISUAL.customTooltipBackgroundColor = Runtime.normalizeColor(colors.customTooltipBackgroundColor);
        Runtime.VISUAL.customTooltipBackgroundAlpha = Runtime.clampCustomTooltipBackgroundAlpha(visual.customTooltipBackgroundAlpha);

        Runtime.VISUAL.fullbright = visual.fullbright;
        Runtime.VISUAL.blockOutline = visual.blockOutline;
        Runtime.VISUAL.blockOutlineColor1 = Runtime.normalizeColor(colors.blockOutlineColor1);
        Runtime.VISUAL.blockOutlineColor2 = Runtime.normalizeColor(colors.blockOutlineColor2);
        Runtime.VISUAL.blockOutlineWidth = Runtime.clampBlockOutlineWidth(visual.blockOutlineWidth);
        Runtime.VISUAL.blockOutlinePhase = visual.blockOutlinePhase;
        Runtime.VISUAL.blockOutlineFilled = visual.blockOutlineFilled;
        Runtime.VISUAL.blockOutlineFilledColor = Runtime.normalizeColor(colors.blockOutlineFilledColor);
        Runtime.VISUAL.blockOutlineFilledAlpha = Runtime.clampBlockOutlineFilledAlpha(visual.blockOutlineFilledAlpha);
        Runtime.VISUAL.hideDefenseIcon = visual.hideDefenseIcon;
        Runtime.VISUAL.disableHeartsShake = visual.disableHeartsShake;

        Runtime.VISUAL.itemAnimations = visual.itemAnimations;
        Runtime.VISUAL.itemAnimationsScale = Runtime.clampItemAnimationsScale(visual.itemAnimationsScale);
        Runtime.VISUAL.itemAnimationsSwingSpeed = Runtime.clampItemAnimationsSwingSpeed(visual.itemAnimationsSwingSpeed);
        Runtime.VISUAL.itemAnimationsIgnoreEffects = visual.itemAnimationsIgnoreEffects;

        Runtime.VISUAL.nameTagTweaks = visual.nameTagTweaks;
        Runtime.VISUAL.nameTagTweaksShowOwn = visual.nameTagTweaksShowOwn;
        Runtime.VISUAL.nameTagTweaksDisableBackground = visual.nameTagTweaksDisableBackground;
        Runtime.VISUAL.nameTagTweaksShadowedText = visual.nameTagTweaksShadowedText;
        Runtime.VISUAL.scoreboard = visual.scoreboard;
        Runtime.VISUAL.scoreboardShadowedText = visual.scoreboardShadowedText;
        Runtime.VISUAL.scoreboardDisableBackground = visual.scoreboardDisableBackground;

        Runtime.VISUAL.effects = visual.effects;
        Runtime.VISUAL.disableGlowEffect = visual.disableGlowEffect;
        Runtime.VISUAL.armorHider = visual.armorHider;
        Runtime.VISUAL.armorHiderSkull = visual.armorHiderSkull;
        Runtime.VISUAL.armorHiderHelmet = visual.armorHiderHelmet;
        Runtime.VISUAL.armorHiderChestplate = visual.armorHiderChestplate;
        Runtime.VISUAL.armorHiderElytra = visual.armorHiderElytra;
        Runtime.VISUAL.armorHiderLeggings = visual.armorHiderLeggings;
        Runtime.VISUAL.armorHiderBoots = visual.armorHiderBoots;
    }

    private static void applyMisc(Config.Misc misc) {
        Runtime.MISC.chatTweaks = misc.chatTweaks;
        Runtime.MISC.chatTweaksCopy = misc.chatTweaksCopy;
        Runtime.MISC.animationFix = misc.animationFix;
        Runtime.MISC.chatNotifications = misc.chatNotifications;
    }

    private static void applyPlayer(Config.Player player) {
        Runtime.PLAYER.autoSprint = player.autoSprint;
    }

    private static void loadModuleKeyBinds(JsonObject object) {
        if (object == null) return;
        Runtime.moduleKeyBinds.clear();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String key = entry.getKey();
            if (key == null || !key.startsWith("keyBind_")) continue;
            Integer code = readKeyBindCode(entry.getValue());
            if (code == null) continue;
            String id = Runtime.normalizeKeyBindId(key.substring("keyBind_".length()));
            if (Runtime.CLICK_GUI_KEY_BIND_ID.equals(id)) {
                Runtime.KEY_BINDS.clickGuiKeyBind = code;
            } else {
                Runtime.moduleKeyBinds.put(id, code);
            }
        }
    }

    private static Integer readKeyBindCode(JsonElement element) {
        try {
            if (element == null || !element.isJsonPrimitive()) return null;
            return element.getAsInt();
        } catch (NumberFormatException | IllegalStateException ignored) {
            return null;
        }
    }
}
