package com.yuki.yuki.clickgui;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SettingRegistry {
    public GuiDefinition.Module module(String name, GuiDefinition.Toggle toggle) {
        return new GuiDefinition.Module(name, toggle);
    }

    public GuiDefinition.Toggle toggle(Supplier<Boolean> get, Consumer<Boolean> set) {
        return new GuiDefinition.Toggle(get, set);
    }

    public GuiDefinition.KeySetting key(String name, Supplier<Integer> get, Consumer<Integer> set) {
        return new GuiDefinition.KeySetting(name, get, set);
    }

    public GuiDefinition.BoolSetting bool(String name, Supplier<Boolean> get, Consumer<Boolean> set) {
        return new GuiDefinition.BoolSetting(name, get, set);
    }

    public GuiDefinition.ColorSetting color(String name, Supplier<Integer> get, Consumer<Integer> preview, Consumer<Integer> set) {
        return new GuiDefinition.ColorSetting(name, get, preview, set);
    }

    public GuiDefinition.IntSliderSetting intSlider(String name, Supplier<Integer> get, Consumer<Integer> preview, Consumer<Integer> save, int min, int max, int step) {
        return new GuiDefinition.IntSliderSetting(name, get, preview, save, min, max, step);
    }

    public GuiDefinition.FloatSliderSetting floatSlider(String name, Supplier<Float> get, Consumer<Float> preview, Consumer<Float> save, float min, float max, float step) {
        return new GuiDefinition.FloatSliderSetting(name, get, preview, save, min, max, step);
    }

    public GuiDefinition.CycleSetting cycle(String name, Supplier<List<String>> options, Supplier<String> get, Consumer<String> set) {
        return new GuiDefinition.CycleSetting(name, options, get, set);
    }
}
