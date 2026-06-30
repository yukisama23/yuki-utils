package com.yuki.yuki.clickgui;

import com.yuki.yuki.config.ConfigManager;
import com.yuki.yuki.config.Runtime;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class GuiDefinition {
    private static final SettingRegistry SETTINGS = new SettingRegistry();
    private static final List<Category> CATEGORIES = createCategories();

    private GuiDefinition() {}

    public static List<Category> categories() {
        return CATEGORIES;
    }

    private static List<Category> createCategories() {
        ArrayList<Category> out = new ArrayList<>();
        out.add(new Category("HUD", List.of(
            moduleArmorHud(),
            moduleEffectHud(),
            moduleReachDisplay(),
            moduleShulkerTooltips(),
            moduleTotemTweaks()
        )));
        out.add(new Category("Visual", List.of(
            moduleArmorHider(),
            moduleBlockOutline(),
            moduleCustomTooltip(),
            moduleEffects(),
            moduleFullbright(),
            moduleHideDefenseIcon(),
            moduleItemAnimations(),
            moduleNameTagTweaks(),
            moduleNoHeartsShake(),
            moduleScoreboard()
        )));
        out.add(new Category("Player", List.of(
            moduleAutoSprint()
        )));
        out.add(new Category("Misc", List.of(
            moduleAnimationFix(),
            moduleChatTweaks(),
            moduleClickGui()
        )));

        for (Category category : out) {
            category.modules.sort(GuiSorting.MODULES);
            for (Module module : category.modules) sortSettings(module);
        }
        return out;
    }

    private static void sortSettings(Module module) {
        module.settings.sort(GuiSorting.SETTINGS);
    }

    private static Module module(String name, Supplier<Boolean> getter, String key) {
        return SETTINGS.module(name, SETTINGS.toggle(getter, value -> ConfigManager.setBoolean(key, value)));
    }

    private static BoolSetting bool(String name, Supplier<Boolean> getter, String key) {
        return SETTINGS.bool(name, getter, value -> ConfigManager.setBoolean(key, value));
    }

    private static ColorSetting color(String name, Supplier<Integer> getter, Consumer<Integer> preview, String key) {
        return SETTINGS.color(name, getter, preview, value -> ConfigManager.setInt(key, value));
    }

    private static Module moduleCustomTooltip() {
        Module m = module("Custom Tooltips", Runtime::isCustomTooltipEnabled, "customTooltip");
        m.settings.add(bool("Border", Runtime::isCustomTooltipBorderEnabled, "customTooltipBorder"));
        m.settings.add(SETTINGS.floatSlider(
            "Width",
            Runtime::getCustomTooltipBorderWidth,
            Runtime::setCustomTooltipBorderWidth,
            value -> ConfigManager.setFloat("customTooltipBorderWidth", Runtime.clampCustomTooltipBorderWidth(value)),
            0.0f,
            5.0f,
            0.5f
        ).visibleWhen(Runtime::isCustomTooltipBorderEnabled));
        m.settings.add(color(
            "Color-1",
            Runtime::getCustomTooltipBorderColor1,
            Runtime::setCustomTooltipBorderColor1,
            "customTooltipBorderColor1"
        ).visibleWhen(Runtime::isCustomTooltipBorderEnabled));
        m.settings.add(color(
            "Color-2",
            Runtime::getCustomTooltipBorderColor2,
            Runtime::setCustomTooltipBorderColor2,
            "customTooltipBorderColor2"
        ).visibleWhen(Runtime::isCustomTooltipBorderEnabled));
        m.settings.add(SETTINGS.intSlider(
            "Roundness",
            Runtime::getCustomTooltipRoundness,
            Runtime::setCustomTooltipRoundness,
            value -> ConfigManager.setInt("customTooltipRoundness", Runtime.clampCustomTooltipRoundness(value)),
            0,
            12,
            1
        ));
        m.settings.add(color(
            "Color",
            Runtime::getCustomTooltipBackgroundColor,
            Runtime::setCustomTooltipBackgroundColor,
            "customTooltipBackgroundColor"
        ));
        m.settings.add(SETTINGS.floatSlider(
            "Alpha",
            Runtime::getCustomTooltipBackgroundAlpha,
            Runtime::setCustomTooltipBackgroundAlpha,
            value -> ConfigManager.setFloat("customTooltipBackgroundAlpha", Runtime.clampCustomTooltipBackgroundAlpha(value)),
            0.0f,
            1.0f,
            0.05f
        ));
        return m;
    }

    private static Module moduleChatTweaks() {
        Module m = module("Chat Tweaks", Runtime::isChatTweaksEnabled, "chatTweaks");
        m.settings.add(bool("Copy", Runtime::isChatTweaksCopyEnabled, "chatTweaksCopy"));
        return m;
    }

    private static Module moduleAnimationFix() {
        return module("Animation Fix", Runtime::isAnimationFixEnabled, "animationFix");
    }

    private static Module moduleArmorHider() {
        Module m = module("Armor Hider", Runtime::isArmorHiderEnabled, "armorHider");
        m.settings.add(bool("Hide Skull", Runtime::isArmorHiderSkullEnabled, "armorHiderSkull"));
        m.settings.add(bool("Hide Helmet", Runtime::isArmorHiderHelmetEnabled, "armorHiderHelmet"));
        m.settings.add(bool("Hide Chestplate", Runtime::isArmorHiderChestplateEnabled, "armorHiderChestplate"));
        m.settings.add(bool("Hide Elytra", Runtime::isArmorHiderElytraEnabled, "armorHiderElytra"));
        m.settings.add(bool("Hide Leggings", Runtime::isArmorHiderLeggingsEnabled, "armorHiderLeggings"));
        m.settings.add(bool("Hide Boots", Runtime::isArmorHiderBootsEnabled, "armorHiderBoots"));
        return m;
    }

    private static Module moduleAutoSprint() {
        return module("Auto Sprint", Runtime::isAutoSprintEnabled, "autoSprint");
    }

    private static Module moduleBlockOutline() {
        Module m = module("Block Outline", Runtime::isBlockOutlineEnabled, "blockOutline");
        m.settings.add(color("Color-1", Runtime::getBlockOutlineColor1, Runtime::setBlockOutlineColor1, "blockOutlineColor1"));
        m.settings.add(color("Color-2", Runtime::getBlockOutlineColor2, Runtime::setBlockOutlineColor2, "blockOutlineColor2"));
        m.settings.add(bool("Phase", Runtime::isBlockOutlinePhaseEnabled, "blockOutlinePhase"));
        m.settings.add(bool("Filled", Runtime::isBlockOutlineFilledEnabled, "blockOutlineFilled"));
        m.settings.add(color(
            "Filled Color",
            Runtime::getBlockOutlineFilledColor,
            Runtime::setBlockOutlineFilledColor,
            "blockOutlineFilledColor"
        ).visibleWhen(Runtime::isBlockOutlineFilledEnabled));
        m.settings.add(SETTINGS.floatSlider(
            "Filled Alpha",
            Runtime::getBlockOutlineFilledAlpha,
            Runtime::setBlockOutlineFilledAlpha,
            value -> ConfigManager.setFloat("blockOutlineFilledAlpha", Runtime.clampBlockOutlineFilledAlpha(value)),
            0.0f,
            1.0f,
            0.05f
        ).visibleWhen(Runtime::isBlockOutlineFilledEnabled));
        m.settings.add(SETTINGS.floatSlider(
            "Outline Width",
            Runtime::getBlockOutlineWidth,
            Runtime::setBlockOutlineWidth,
            value -> ConfigManager.setFloat("blockOutlineWidth", Runtime.clampBlockOutlineWidth(value)),
            1.0f,
            2.0f,
            0.1f
        ));
        return m;
    }

    private static Module moduleFullbright() {
        return module("Fullbright", Runtime::isFullbrightEnabled, "fullbright");
    }

    private static Module moduleTotemTweaks() {
        Module m = module("Totem Tweaks", Runtime::isTotemTweaksEnabled, "totemTweaks");
        m.settings.add(bool("Disable Animations", Runtime::isTotemTweaksDisableAnimationsEnabled, "totemTweaksDisableAnimations"));
        m.settings.add(bool("Totem Count", Runtime::isTotemTweaksTotemCountEnabled, "totemTweaksTotemCount"));
        m.settings.add(bool("Totem Alert", Runtime::isTotemTweaksTotemAlertEnabled, "totemTweaksTotemAlert"));
        return m;
    }

    private static Module moduleEffects() {
        Module m = module("Effects", Runtime::isEffectsEnabled, "effects");
        m.settings.add(bool("Disable Glow Effect", Runtime::isGlowEffectDisabled, "disableGlowEffect"));
        return m;
    }

    private static Module moduleHideDefenseIcon() {
        return module("Hide Defense Icon", Runtime::isHideDefenseIconEnabled, "hideDefenseIcon");
    }

    private static Module moduleItemAnimations() {
        Module m = module("Item Animations", Runtime::isItemAnimationsEnabled, "itemAnimations");
        m.settings.add(SETTINGS.floatSlider(
            "Scale",
            Runtime::getItemAnimationsScale,
            Runtime::setItemAnimationsScale,
            value -> ConfigManager.setFloat("itemAnimationsScale", Runtime.clampItemAnimationsScale(value)),
            -1.0f,
            1.0f,
            0.1f
        ));
        m.settings.add(SETTINGS.floatSlider(
            "Swing Speed",
            Runtime::getItemAnimationsSwingSpeed,
            Runtime::setItemAnimationsSwingSpeed,
            value -> ConfigManager.setFloat("itemAnimationsSwingSpeed", Runtime.clampItemAnimationsSwingSpeed(value)),
            -1.0f,
            1.0f,
            0.1f
        ));
        m.settings.add(bool("Ignore Effects", Runtime::isItemAnimationsIgnoreEffectsEnabled, "itemAnimationsIgnoreEffects"));
        return m;
    }

    private static Module moduleNameTagTweaks() {
        Module m = module("Nametag Tweaks", Runtime::isNameTagTweaksEnabled, "nameTagTweaks");
        m.settings.add(bool("Own Nametag", Runtime::isNameTagTweaksShowOwnEnabled, "nameTagTweaksShowOwn"));
        m.settings.add(bool("No Background", Runtime::isNameTagTweaksDisableBackgroundEnabled, "nameTagTweaksDisableBackground"));
        m.settings.add(bool("Shadowed Text", Runtime::isNameTagTweaksShadowedTextEnabled, "nameTagTweaksShadowedText"));
        return m;
    }

    private static Module moduleNoHeartsShake() {
        return module("No Hearts Shake", Runtime::isNoHeartsShakeEnabled, "disableHeartsShake");
    }

    private static Module moduleClickGui() {
        Module m = SETTINGS.module("Click GUI", null);
        m.settings.add(SETTINGS.key("Keybind", () -> Runtime.getModuleKeyBind("Click GUI"), value -> {
            Runtime.setModuleKeyBind("Click GUI", value);
            ConfigManager.setInt(Runtime.getModuleKeyBindConfigKey("Click GUI"), value);
        }));
        m.settings.add(color("GUI Color-1", Runtime::getClickGuiColor, Runtime::setClickGuiColor, "clickGuiColor"));
        m.settings.add(color("GUI Color-2", Runtime::getClickGuiColor2, Runtime::setClickGuiColor2, "clickGuiColor2"));
        m.settings.add(bool("Chat Notifications", Runtime::isChatNotificationsEnabled, "chatNotifications"));
        return m;
    }

    private static Module moduleArmorHud() {
        Module m = module("Armor HUD", Runtime::isArmorHudEnabled, "armorHud");
        m.settings.add(SETTINGS.cycle(
            "Armor",
            Runtime::getArmorHudStyleOptions,
            Runtime::getArmorHudStyle,
            value -> ConfigManager.setString("armorHudStyle", Runtime.normalizeArmorHudStyle(value))
        ));
        m.settings.add(SETTINGS.cycle(
            "Offhand",
            Runtime::getArmorHudOffhandStyleOptions,
            Runtime::getArmorHudOffhandStyle,
            value -> ConfigManager.setString("armorHudOffhandStyle", Runtime.normalizeArmorHudOffhandStyle(value))
        ));
        m.settings.add(SETTINGS.cycle(
            "Position",
            Runtime::getArmorHudPositionOptions,
            Runtime::getArmorHudPosition,
            value -> ConfigManager.setString("armorHudPosition", Runtime.normalizeArmorHudPosition(value))
        ));
        return m;
    }

    private static Module moduleEffectHud() {
        return module("Effect HUD", Runtime::isEffectHudEnabled, "effectHud");
    }

    private static Module moduleShulkerTooltips() {
        Module m = module("Shulker Tooltips", Runtime::isShulkerTooltipsEnabled, "shulkerTooltips");
        m.settings.add(bool("Shift Only", Runtime::isShulkerTooltipsShiftOnlyEnabled, "shulkerTooltipsShiftOnly"));
        return m;
    }

    private static Module moduleReachDisplay() {
        return module("Reach Display", Runtime::isReachDisplayHudEnabled, "reachDisplayHud");
    }

    private static Module moduleScoreboard() {
        Module m = module("Scoreboard", Runtime::isScoreboardEnabled, "scoreboard");
        m.settings.add(bool("Shadowed Text", Runtime::isScoreboardShadowedTextEnabled, "scoreboardShadowedText"));
        m.settings.add(bool("No Background", Runtime::isScoreboardDisableBackgroundEnabled, "scoreboardDisableBackground"));
        return m;
    }

    public static final class Category {
        public final String name;
        public final String displayName;
        public final List<Module> modules;

        public Category(String name, List<Module> modules) {
            this.name = name;
            this.displayName = visibleText(name);
            this.modules = new ArrayList<>(modules);
        }
    }

    public static final class Module {
        public final String name;
        public final String displayName;
        public final Toggle toggle;
        public final ArrayList<Setting> settings = new ArrayList<>();

        public Module(String name, Toggle toggle) {
            this.name = name;
            this.displayName = visibleText(name);
            this.toggle = toggle;
        }

        public boolean hasToggle() {
            return toggle != null;
        }

        public boolean active() {
            return toggle != null && toggle.get.get();
        }

        public boolean toggle() {
            if (toggle == null) return false;
            boolean next = !toggle.get.get();
            toggle.set.accept(next);
            return toggle.get.get();
        }
    }

    public static final class Toggle {
        public final Supplier<Boolean> get;
        public final Consumer<Boolean> set;

        public Toggle(Supplier<Boolean> get, Consumer<Boolean> set) {
            this.get = get;
            this.set = set;
        }
    }

    public abstract static class Setting {
        public final String name;
        private BooleanSupplier visible = () -> true;

        public Setting(String name) {
            this.name = visibleText(name);
        }

        public boolean visible() {
            return visible.getAsBoolean();
        }

        public Setting visibleWhen(BooleanSupplier visible) {
            this.visible = visible == null ? () -> true : visible;
            return this;
        }

        public String text() {
            return "";
        }
    }

    public static final class ColorSetting extends Setting {
        public final Supplier<Integer> get;
        public final Consumer<Integer> preview;
        public final Consumer<Integer> set;

        public ColorSetting(String name, Supplier<Integer> get, Consumer<Integer> preview, Consumer<Integer> set) {
            super(name);
            this.get = get;
            this.preview = preview;
            this.set = set;
        }

        public void previewColor(int color) {
            preview.accept(color);
        }

        public void setColor(int color) {
            set.accept(color);
        }

    }

    public static class KeySetting extends Setting {
        public final Supplier<Integer> get;
        public final Consumer<Integer> set;

        public KeySetting(String name, Supplier<Integer> get, Consumer<Integer> set) {
            super(name);
            this.get = get;
            this.set = set;
        }

        public String text() {
            return Runtime.getKeyBindName(get.get());
        }

        public void set(int code) {
            set.accept(code);
        }
    }

    public static final class BoolSetting extends Setting {
        public final Supplier<Boolean> get;
        public final Consumer<Boolean> set;

        public BoolSetting(String name, Supplier<Boolean> get, Consumer<Boolean> set) {
            super(name);
            this.get = get;
            this.set = set;
        }
    }

    public abstract static class SliderSetting extends Setting {
        public final float min;
        public final float max;
        public final float step;

        public SliderSetting(String name, float min, float max, float step) {
            super(name);
            this.min = min;
            this.max = max;
            this.step = step <= 0.0f ? 1.0f : step;
        }

        public abstract float getFloat();
        public abstract void previewFloat(float value);
        public abstract void saveFloat(float value);

        public void setTypedValue(float value) {
            float clamped = MathHelper.clamp(value, min, max);
            previewTypedValue(clamped);
            saveTypedValue(clamped);
        }

        public void saveCurrent() {
            saveFloat(getFloat());
        }

        protected abstract void previewTypedValue(float value);
        protected abstract void saveTypedValue(float value);
        public abstract String text();
    }

    public static final class IntSliderSetting extends SliderSetting {
        public final Supplier<Integer> get;
        public final Consumer<Integer> preview;
        public final Consumer<Integer> save;

        public IntSliderSetting(String name, Supplier<Integer> get, Consumer<Integer> preview, Consumer<Integer> save, int min, int max, int step) {
            super(name, min, max, step);
            this.get = get;
            this.preview = preview;
            this.save = save;
        }

        @Override
        public float getFloat() {
            return get.get();
        }

        @Override
        public void previewFloat(float value) {
            int v = Math.round(snap(value, min, max, step));
            preview.accept(v);
        }

        @Override
        public void saveFloat(float value) {
            int v = Math.round(snap(value, min, max, step));
            save.accept(v);
        }

        @Override
        protected void previewTypedValue(float value) {
            preview.accept(Math.round(MathHelper.clamp(value, min, max)));
        }

        @Override
        protected void saveTypedValue(float value) {
            save.accept(Math.round(MathHelper.clamp(value, min, max)));
        }

        @Override
        public String text() {
            return String.valueOf(get.get());
        }
    }

    public static final class FloatSliderSetting extends SliderSetting {
        public final Supplier<Float> get;
        public final Consumer<Float> preview;
        public final Consumer<Float> save;

        public FloatSliderSetting(String name, Supplier<Float> get, Consumer<Float> preview, Consumer<Float> save, float min, float max, float step) {
            super(name, min, max, step);
            this.get = get;
            this.preview = preview;
            this.save = save;
        }

        @Override
        public float getFloat() {
            return get.get();
        }

        @Override
        public void previewFloat(float value) {
            float v = snap(value, min, max, step);
            preview.accept(v);
        }

        @Override
        public void saveFloat(float value) {
            float v = snap(value, min, max, step);
            save.accept(v);
        }

        @Override
        protected void previewTypedValue(float value) {
            preview.accept(MathHelper.clamp(value, min, max));
        }

        @Override
        protected void saveTypedValue(float value) {
            save.accept(MathHelper.clamp(value, min, max));
        }

        @Override
        public String text() {
            return trim(get.get());
        }
    }

    public static final class CycleSetting extends Setting {
        public final Supplier<List<String>> options;
        public final Supplier<String> get;
        public final Consumer<String> set;

        public CycleSetting(String name, Supplier<List<String>> options, Supplier<String> get, Consumer<String> set) {
            super(name);
            this.options = options;
            this.get = get;
            this.set = set;
        }

        public void next(int amount) {
            List<String> list = options.get();
            if (list == null || list.isEmpty()) return;
            String current = get.get();
            int idx = list.indexOf(current);
            if (idx < 0) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).equalsIgnoreCase(current)) {
                        idx = i;
                        break;
                    }
                }
            }
            if (idx < 0) idx = 0;
            idx = Math.floorMod(idx + amount, list.size());
            set.accept(list.get(idx));
        }
    }

    private static float snap(float value, float min, float max, float step) {
        float clamped = MathHelper.clamp(value, min, max);
        float snapped = min + Math.round((clamped - min) / step) * step;
        return MathHelper.clamp(snapped, min, max);
    }

    private static String trim(float value) {
        if (Math.abs(value - Math.round(value)) < 0.0001f) return String.valueOf(Math.round(value));
        return String.format(Locale.ROOT, "%.2f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private static String visibleText(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT);
    }

}
