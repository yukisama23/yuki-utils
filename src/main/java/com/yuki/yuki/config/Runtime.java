package com.yuki.yuki.config;

import com.yuki.yuki.util.ColorUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

public final class Runtime {
    public static final int DEFAULT_GRADIENT_COLOR_1 = DefaultColors.GRADIENT_LEFT;
    public static final int DEFAULT_GRADIENT_COLOR_2 = DefaultColors.GRADIENT_RIGHT;
    public static final int DEFAULT_CUSTOM_TOOLTIP_BACKGROUND_COLOR = DefaultColors.CUSTOM_TOOLTIP_BACKGROUND;
    public static final int DEFAULT_CLICK_GUI_COLOR = DefaultColors.CLICK_GUI_LEFT;
    public static final int DEFAULT_CLICK_GUI_COLOR_2 = DefaultColors.CLICK_GUI_RIGHT;
    public static final int DEFAULT_BLOCK_OUTLINE_FILLED_COLOR = DefaultColors.GRADIENT_LEFT;
    public static final int DEFAULT_CLICK_GUI_KEY_BIND = 344;
    static final RuntimeState.Color COLORS = new RuntimeState.Color();
    static final RuntimeState.Hud HUD = new RuntimeState.Hud();
    static final RuntimeState.Misc MISC = new RuntimeState.Misc();
    static final RuntimeState.Visual VISUAL = new RuntimeState.Visual();
    static final RuntimeState.Player PLAYER = new RuntimeState.Player();
    static final RuntimeState.KeyBinds KEY_BINDS = new RuntimeState.KeyBinds();
    static final int KEY_BIND_NONE = 0;
    static final String CLICK_GUI_KEY_BIND_ID = "click_gui";

    static final int MOUSE_BIND_OFFSET = -100;
    static final ConcurrentHashMap<String, Integer> moduleKeyBinds = new ConcurrentHashMap<>();

    private static final String PREFIX_TEXT = "[yuki]";

    private Runtime() {}

    public static void loadFromDisk(Path path) {
        RuntimeConfigLoader.load(path);
    }

    public static Text feedbackText(String message) {
        String body = message == null ? "" : message.trim().toLowerCase(Locale.ROOT);
        MutableText out = prefixText();
        if (!body.isEmpty()) {
            out.append(Text.literal(" "));
            out.append(Text.literal(body).formatted(Formatting.GRAY));
        }
        return out;
    }

    private static MutableText prefixText() {
        return gradientText(PREFIX_TEXT, COLORS.clickGuiColor, COLORS.clickGuiColor2);
    }

    private static MutableText gradientText(String text, int color1, int color2) {
        MutableText out = Text.empty();
        if (text == null || text.isEmpty()) return out;
        int[] codePoints = text.codePoints().toArray();
        int last = Math.max(1, codePoints.length - 1);
        for (int i = 0; i < codePoints.length; i++) {
            float progress = codePoints.length <= 1 ? 0.0f : i / (float) last;
            int color = ColorUtil.lerp(color1, color2, progress) & 0x00FFFFFF;
            out.append(Text.literal(new String(Character.toChars(codePoints[i])))
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(color))));
        }
        return out;
    }

    public static int getClickGuiColor() {
        return COLORS.clickGuiColor;
    }

    public static void setClickGuiColor(int color) {
        COLORS.clickGuiColor = normalizeColor(color);
    }

    public static int getClickGuiColor2() {
        return COLORS.clickGuiColor2;
    }

    public static void setClickGuiColor2(int color) {
        COLORS.clickGuiColor2 = normalizeColor(color);
    }

    public static boolean isTotemTweaksEnabled() {
        return HUD.totemTweaks;
    }

    public static boolean isTotemTweaksDisableAnimationsEnabled() {
        return HUD.totemTweaksDisableAnimations;
    }

    public static boolean isTotemTweaksTotemCountEnabled() {
        return HUD.totemTweaksTotemCount;
    }

    public static boolean isTotemTweaksTotemAlertEnabled() {
        return HUD.totemTweaksTotemAlert;
    }

    public static int getTotemTweaksHudX() {
        return HUD.totemTweaksHudX;
    }

    public static int getTotemTweaksHudY() {
        return HUD.totemTweaksHudY;
    }

    public static float getTotemTweaksHudScale() {
        return HUD.totemTweaksHudScale;
    }

    public static int getTotemTweaksAlertHudX() {
        return HUD.totemTweaksAlertHudX;
    }

    public static int getTotemTweaksAlertHudY() {
        return HUD.totemTweaksAlertHudY;
    }

    public static float getTotemTweaksAlertHudScale() {
        return HUD.totemTweaksAlertHudScale;
    }

    public static boolean isChatTweaksEnabled() {
        return MISC.chatTweaks;
    }

    public static boolean isChatTweaksCopyEnabled() {
        return MISC.chatTweaksCopy;
    }

    public static boolean isAnimationFixEnabled() {
        return MISC.animationFix;
    }

    public static boolean isCustomTooltipEnabled() {
        return VISUAL.customTooltip;
    }

    public static boolean isCustomTooltipBorderEnabled() {
        return VISUAL.customTooltipBorder;
    }

    public static float getCustomTooltipBorderWidth() {
        return VISUAL.customTooltipBorderWidth;
    }

    public static int getCustomTooltipBorderColor1() {
        return VISUAL.customTooltipBorderColor1;
    }

    public static int getCustomTooltipBorderColor2() {
        return VISUAL.customTooltipBorderColor2;
    }

    public static int getCustomTooltipRoundness() {
        return VISUAL.customTooltipRoundness;
    }

    public static int getCustomTooltipBackgroundColor() {
        return VISUAL.customTooltipBackgroundColor;
    }

    public static float getCustomTooltipBackgroundAlpha() {
        return VISUAL.customTooltipBackgroundAlpha;
    }

    public static int getCustomTooltipBackgroundColorWithAlpha() {
        int alpha = MathHelper.clamp(Math.round(VISUAL.customTooltipBackgroundAlpha * 255.0f), 0, 255);
        return (alpha << 24) | (VISUAL.customTooltipBackgroundColor & 0x00FFFFFF);
    }

    public static boolean isFullbrightEnabled() {
        return VISUAL.fullbright;
    }

    public static boolean isHideDefenseIconEnabled() {
        return VISUAL.hideDefenseIcon;
    }

    public static boolean isBlockOutlineEnabled() {
        return VISUAL.blockOutline;
    }

    public static int getBlockOutlineColor1() {
        return VISUAL.blockOutlineColor1;
    }

    public static int getBlockOutlineColor2() {
        return VISUAL.blockOutlineColor2;
    }

    public static float getBlockOutlineWidth() {
        return VISUAL.blockOutlineWidth;
    }

    public static float getBlockOutlineRenderWidth() {
        return VISUAL.blockOutlineWidth * 2.0f;
    }

    public static boolean isBlockOutlinePhaseEnabled() {
        return VISUAL.blockOutlinePhase;
    }

    public static boolean isBlockOutlineFilledEnabled() {
        return VISUAL.blockOutlineFilled;
    }

    public static int getBlockOutlineFilledColor() {
        return VISUAL.blockOutlineFilledColor;
    }

    public static float getBlockOutlineFilledAlpha() {
        return VISUAL.blockOutlineFilledAlpha;
    }

    public static int getBlockOutlineFilledArgb() {
        int alpha = MathHelper.clamp(Math.round(VISUAL.blockOutlineFilledAlpha * 255.0f), 0, 255);
        return (alpha << 24) | (VISUAL.blockOutlineFilledColor & 0x00FFFFFF);
    }

    public static boolean isNoHeartsShakeEnabled() {
        return VISUAL.disableHeartsShake;
    }

    public static void setBlockOutlineColor1(int color) {
        VISUAL.blockOutlineColor1 = normalizeColor(color);
    }

    public static void setBlockOutlineColor2(int color) {
        VISUAL.blockOutlineColor2 = normalizeColor(color);
    }

    public static void setBlockOutlineWidth(float width) {
        VISUAL.blockOutlineWidth = clampBlockOutlineWidth(width);
    }

    public static void setBlockOutlineFilledColor(int color) {
        VISUAL.blockOutlineFilledColor = normalizeColor(color);
    }

    public static void setBlockOutlineFilledAlpha(float alpha) {
        VISUAL.blockOutlineFilledAlpha = clampBlockOutlineFilledAlpha(alpha);
    }

    public static void setTotemTweaksHudX(int x) {
        HUD.totemTweaksHudX = x;
    }

    public static void setTotemTweaksHudY(int y) {
        HUD.totemTweaksHudY = y;
    }

    public static void setTotemTweaksHudScale(float scale) {
        HUD.totemTweaksHudScale = clampHudScale(scale);
    }

    public static void setTotemTweaksAlertHudX(int x) {
        HUD.totemTweaksAlertHudX = x;
    }

    public static void setTotemTweaksAlertHudY(int y) {
        HUD.totemTweaksAlertHudY = y;
    }

    public static void setTotemTweaksAlertHudScale(float scale) {
        HUD.totemTweaksAlertHudScale = clampHudScale(scale);
    }

    public static void setCustomTooltipBorderWidth(float width) {
        VISUAL.customTooltipBorderWidth = clampCustomTooltipBorderWidth(width);
    }

    public static void setCustomTooltipBorderColor1(int color) {
        VISUAL.customTooltipBorderColor1 = normalizeColor(color);
    }

    public static void setCustomTooltipBorderColor2(int color) {
        VISUAL.customTooltipBorderColor2 = normalizeColor(color);
    }

    public static void setCustomTooltipRoundness(int roundness) {
        VISUAL.customTooltipRoundness = clampCustomTooltipRoundness(roundness);
    }

    public static void setCustomTooltipBackgroundColor(int color) {
        VISUAL.customTooltipBackgroundColor = normalizeColor(color);
    }

    public static void setCustomTooltipBackgroundAlpha(float alpha) {
        VISUAL.customTooltipBackgroundAlpha = clampCustomTooltipBackgroundAlpha(alpha);
    }

    public static boolean isAutoSprintEnabled() {
        return PLAYER.autoSprint;
    }

    public static int getKeyBindNone() {
        return RuntimeKeyBinds.none();
    }

    public static int mouseButtonToKeyBind(int button) {
        return RuntimeKeyBinds.fromMouseButton(button);
    }

    public static boolean isMouseKeyBind(int code) {
        return RuntimeKeyBinds.isMouseBind(code);
    }

    public static int keyBindToMouseButton(int code) {
        return RuntimeKeyBinds.toMouseButton(code);
    }

    public static String getModuleKeyBindConfigKey(String moduleName) {
        return RuntimeKeyBinds.configKey(moduleName);
    }

    public static int getClickGuiKeyBind() {
        return RuntimeKeyBinds.clickGui();
    }

    public static int getModuleKeyBind(String moduleName) {
        return RuntimeKeyBinds.get(moduleName);
    }

    public static void setModuleKeyBind(String moduleName, int code) {
        RuntimeKeyBinds.set(moduleName, code);
    }

    static String normalizeKeyBindId(String id) {
        return RuntimeKeyBinds.normalizeId(id);
    }

    public static String getKeyBindName(int code) {
        return RuntimeKeyBinds.name(code);
    }

    public static boolean isItemAnimationsEnabled() {
        return VISUAL.itemAnimations;
    }

    public static float getItemAnimationsScale() {
        return VISUAL.itemAnimationsScale;
    }

    public static float getItemAnimationsSwingSpeed() {
        return VISUAL.itemAnimationsSwingSpeed;
    }

    public static boolean isItemAnimationsIgnoreEffectsEnabled() {
        return VISUAL.itemAnimationsIgnoreEffects;
    }

    public static boolean isNameTagTweaksEnabled() {
        return VISUAL.nameTagTweaks;
    }

    public static boolean isNameTagTweaksShowOwnEnabled() {
        return VISUAL.nameTagTweaksShowOwn;
    }

    public static boolean isNameTagTweaksDisableBackgroundEnabled() {
        return VISUAL.nameTagTweaksDisableBackground;
    }

    public static boolean isNameTagTweaksShadowedTextEnabled() {
        return VISUAL.nameTagTweaksShadowedText;
    }

    public static boolean isScoreboardEnabled() {
        return VISUAL.scoreboard;
    }

    public static boolean isScoreboardShadowedTextEnabled() {
        return VISUAL.scoreboardShadowedText;
    }

    public static boolean isScoreboardDisableBackgroundEnabled() {
        return VISUAL.scoreboardDisableBackground;
    }

    public static boolean isChatNotificationsEnabled() {
        return MISC.chatNotifications;
    }

    public static void setItemAnimationsScale(float v) {
        VISUAL.itemAnimationsScale = clampItemAnimationsScale(v);
    }

    public static void setItemAnimationsSwingSpeed(float v) {
        VISUAL.itemAnimationsSwingSpeed = clampItemAnimationsSwingSpeed(v);
    }

    public static boolean isReachDisplayHudEnabled() {
        return HUD.reachDisplayHud;
    }

    public static int getReachDisplayHudX() {
        return HUD.reachDisplayHudX;
    }

    public static int getReachDisplayHudY() {
        return HUD.reachDisplayHudY;
    }

    public static float getReachDisplayHudScale() {
        return HUD.reachDisplayHudScale;
    }

    public static void setReachDisplayHudX(int x) {
        HUD.reachDisplayHudX = x;
    }

    public static void setReachDisplayHudY(int y) {
        HUD.reachDisplayHudY = y;
    }

    public static void setReachDisplayHudScale(float scale) {
        HUD.reachDisplayHudScale = clampHudScale(scale);
    }

    public static boolean isArmorHudEnabled() {
        return HUD.armorHud;
    }

    public static List<String> getArmorHudStyleOptions() {
        return RuntimeHudOptions.armorStyleOptions();
    }

    public static String getArmorHudStyle() {
        return RuntimeHudOptions.armorStyle();
    }

    public static String normalizeArmorHudStyle(String style) {
        return RuntimeHudOptions.normalizeArmorStyle(style);
    }

    public static boolean isArmorHudHotbarStyle() {
        return RuntimeHudOptions.isArmorHotbarStyle();
    }

    public static List<String> getArmorHudOffhandStyleOptions() {
        return RuntimeHudOptions.offhandStyleOptions();
    }

    public static String getArmorHudOffhandStyle() {
        return RuntimeHudOptions.offhandStyle();
    }

    public static String normalizeArmorHudOffhandStyle(String style) {
        return RuntimeHudOptions.normalizeOffhandStyle(style);
    }

    public static boolean isArmorHudOffhandEnabled() {
        return RuntimeHudOptions.isOffhandEnabled();
    }

    public static boolean isArmorHudOffhandHotbarStyle() {
        return RuntimeHudOptions.isOffhandHotbarStyle();
    }

    public static List<String> getArmorHudPositionOptions() {
        return RuntimeHudOptions.positionOptions();
    }

    public static String getArmorHudPosition() {
        return RuntimeHudOptions.position();
    }

    public static String normalizeArmorHudPosition(String position) {
        return RuntimeHudOptions.normalizePosition(position);
    }

    public static boolean isArmorHudCustomPosition() {
        return RuntimeHudOptions.isCustomPosition();
    }

    public static int getArmorHudX() {
        return HUD.armorHudX;
    }

    public static int getArmorHudY() {
        return HUD.armorHudY;
    }

    public static float getArmorHudScale() {
        return HUD.armorHudScale;
    }

    public static void setArmorHudX(int x) {
        HUD.armorHudX = x;
    }

    public static void setArmorHudY(int y) {
        HUD.armorHudY = y;
    }

    public static void setArmorHudScale(float scale) {
        HUD.armorHudScale = clampHudScale(scale);
    }

    public static int getArmorHudOffhandX() {
        return HUD.armorHudOffhandX;
    }

    public static int getArmorHudOffhandY() {
        return HUD.armorHudOffhandY;
    }

    public static float getArmorHudOffhandScale() {
        return HUD.armorHudOffhandScale;
    }

    public static void setArmorHudOffhandX(int x) {
        HUD.armorHudOffhandX = x;
    }

    public static void setArmorHudOffhandY(int y) {
        HUD.armorHudOffhandY = y;
    }

    public static void setArmorHudOffhandScale(float scale) {
        HUD.armorHudOffhandScale = clampHudScale(scale);
    }

    public static boolean shouldHideVanillaOffhandHud() {
        return isArmorHudOffhandEnabled();
    }

    public static boolean shouldRenderFixedOffhandHud() {
        return isArmorHudOffhandEnabled() && !isArmorHudCustomPosition();
    }

    public static boolean isEffectHudEnabled() {
        return HUD.effectHud;
    }

    public static boolean isShulkerTooltipsEnabled() {
        return HUD.shulkerTooltips;
    }

    public static boolean isShulkerTooltipsShiftOnlyEnabled() {
        return HUD.shulkerTooltipsShiftOnly;
    }

    public static int getEffectHudX() {
        return HUD.effectHudX;
    }

    public static int getEffectHudY() {
        return HUD.effectHudY;
    }

    public static float getEffectHudScale() {
        return HUD.effectHudScale;
    }

    public static void setEffectHudX(int x) {
        HUD.effectHudX = x;
    }

    public static void setEffectHudY(int y) {
        HUD.effectHudY = y;
    }

    public static void setEffectHudScale(float scale) {
        HUD.effectHudScale = clampHudScale(scale);
    }

    public static boolean shouldHideVanillaInventoryEffects() {
        return HUD.effectHud;
    }

    public static boolean isEffectsEnabled() {
        return VISUAL.effects;
    }

    public static boolean isGlowEffectDisabled() {
        return VISUAL.disableGlowEffect;
    }

    public static boolean shouldDisableGlowEffect() {
        return VISUAL.effects && VISUAL.disableGlowEffect;
    }

    public static boolean isArmorHiderEnabled() {
        return VISUAL.armorHider;
    }

    public static boolean isArmorHiderSkullEnabled() {
        return VISUAL.armorHiderSkull;
    }

    public static boolean isArmorHiderHelmetEnabled() {
        return VISUAL.armorHiderHelmet;
    }

    public static boolean isArmorHiderChestplateEnabled() {
        return VISUAL.armorHiderChestplate;
    }

    public static boolean isArmorHiderElytraEnabled() {
        return VISUAL.armorHiderElytra;
    }

    public static boolean isArmorHiderLeggingsEnabled() {
        return VISUAL.armorHiderLeggings;
    }

    public static boolean isArmorHiderBootsEnabled() {
        return VISUAL.armorHiderBoots;
    }

    static int normalizeColor(int color) {
        return 0xFF000000 | (color & 0x00FFFFFF);
    }

    public static float clampCustomTooltipBorderWidth(float v) {
        return MathHelper.clamp(v, 0.0f, 5.0f);
    }

    public static float clampBlockOutlineWidth(float v) {
        float clamped = MathHelper.clamp(v, 1.0f, 2.0f);
        return Math.round(clamped * 10.0f) / 10.0f;
    }

    public static float clampBlockOutlineFilledAlpha(float v) {
        float clamped = MathHelper.clamp(v, 0.0f, 1.0f);
        return Math.round(clamped * 100.0f) / 100.0f;
    }

    public static int clampCustomTooltipRoundness(int v) {
        return MathHelper.clamp(v, 0, 12);
    }

    public static float clampCustomTooltipBackgroundAlpha(float v) {
        return MathHelper.clamp(v, 0.0f, 1.0f);
    }

    static float clampHudScale(float v) {
        return MathHelper.clamp(v, 0.25f, 5.0f);
    }

    public static float clampItemAnimationsScale(float v) {
        float clamped = MathHelper.clamp(v, -1.0f, 1.0f);
        return Math.round(clamped * 10.0f) / 10.0f;
    }

    public static float clampItemAnimationsSwingSpeed(float v) {
        float clamped = MathHelper.clamp(v, -1.0f, 1.0f);
        return Math.round(clamped * 10.0f) / 10.0f;
    }

}
