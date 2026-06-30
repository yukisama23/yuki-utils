package com.yuki.yuki.clickgui;

import java.util.Comparator;
import java.util.Locale;

public final class GuiSorting {
    public static final Comparator<GuiDefinition.Module> MODULES = Comparator.comparing(module -> key(module.name));
    public static final Comparator<GuiDefinition.Setting> SETTINGS = Comparator.comparing(setting -> key(setting.name));

    private GuiSorting() {}

    private static String key(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
