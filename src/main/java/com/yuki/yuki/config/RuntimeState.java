package com.yuki.yuki.config;

final class RuntimeState {
    private RuntimeState() {}

    static final class Color {
        volatile int clickGuiColor = Runtime.DEFAULT_CLICK_GUI_COLOR;
        volatile int clickGuiColor2 = Runtime.DEFAULT_CLICK_GUI_COLOR_2;
    }

    static final class Hud {
        volatile boolean totemTweaks;
        volatile boolean totemTweaksDisableAnimations;
        volatile boolean totemTweaksTotemCount;
        volatile boolean totemTweaksTotemAlert;
        volatile int totemTweaksHudX = -1;
        volatile int totemTweaksHudY = -1;
        volatile float totemTweaksHudScale = 1.0f;
        volatile int totemTweaksAlertHudX = -1;
        volatile int totemTweaksAlertHudY = -1;
        volatile float totemTweaksAlertHudScale = 1.0f;
        volatile boolean reachDisplayHud;
        volatile int reachDisplayHudX = -1;
        volatile int reachDisplayHudY = -1;
        volatile float reachDisplayHudScale = 1.0f;
        volatile boolean armorHud;
        volatile String armorHudStyle = "hotbar";
        volatile String armorHudOffhandStyle = "hotbar";
        volatile String armorHudPosition = "hotbar_left";
        volatile int armorHudX = -1;
        volatile int armorHudY = -1;
        volatile float armorHudScale = 1.0f;
        volatile int armorHudOffhandX = -1;
        volatile int armorHudOffhandY = -1;
        volatile float armorHudOffhandScale = 1.0f;
        volatile boolean effectHud;
        volatile int effectHudX = -1;
        volatile int effectHudY = -1;
        volatile float effectHudScale = 1.0f;
        volatile boolean shulkerTooltips;
        volatile boolean shulkerTooltipsShiftOnly;
    }

    static final class Misc {
        volatile boolean chatTweaks;
        volatile boolean chatTweaksCopy;
        volatile boolean animationFix;
        volatile boolean chatNotifications;
    }

    static final class Visual {
        volatile boolean customTooltip = true;
        volatile boolean customTooltipBorder = true;
        volatile float customTooltipBorderWidth = 2.0f;
        volatile int customTooltipBorderColor1 = Runtime.DEFAULT_GRADIENT_COLOR_1;
        volatile int customTooltipBorderColor2 = Runtime.DEFAULT_GRADIENT_COLOR_2;
        volatile int customTooltipRoundness = 12;
        volatile int customTooltipBackgroundColor = Runtime.DEFAULT_CUSTOM_TOOLTIP_BACKGROUND_COLOR;
        volatile float customTooltipBackgroundAlpha = 0.85f;
        volatile boolean fullbright = true;
        volatile boolean blockOutline;
        volatile int blockOutlineColor1 = Runtime.DEFAULT_GRADIENT_COLOR_1;
        volatile int blockOutlineColor2 = Runtime.DEFAULT_GRADIENT_COLOR_2;
        volatile float blockOutlineWidth = 2.0f;
        volatile boolean blockOutlinePhase;
        volatile boolean blockOutlineFilled;
        volatile int blockOutlineFilledColor = Runtime.DEFAULT_BLOCK_OUTLINE_FILLED_COLOR;
        volatile float blockOutlineFilledAlpha = 0.2f;
        volatile boolean hideDefenseIcon;
        volatile boolean disableHeartsShake;
        volatile boolean itemAnimations;
        volatile float itemAnimationsScale;
        volatile float itemAnimationsSwingSpeed;
        volatile boolean itemAnimationsIgnoreEffects;
        volatile boolean nameTagTweaks;
        volatile boolean nameTagTweaksShowOwn;
        volatile boolean nameTagTweaksDisableBackground;
        volatile boolean nameTagTweaksShadowedText;
        volatile boolean scoreboard;
        volatile boolean scoreboardShadowedText;
        volatile boolean scoreboardDisableBackground;
        volatile boolean effects;
        volatile boolean disableGlowEffect;
        volatile boolean armorHider;
        volatile boolean armorHiderSkull;
        volatile boolean armorHiderHelmet;
        volatile boolean armorHiderChestplate;
        volatile boolean armorHiderElytra;
        volatile boolean armorHiderLeggings;
        volatile boolean armorHiderBoots;
    }

    static final class Player {
        volatile boolean autoSprint = true;
    }

    static final class KeyBinds {
        volatile int clickGuiKeyBind = Runtime.DEFAULT_CLICK_GUI_KEY_BIND;
    }
}
