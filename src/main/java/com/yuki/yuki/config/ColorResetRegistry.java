package com.yuki.yuki.config;

import java.util.List;
import java.util.function.IntConsumer;

public final class ColorResetRegistry {
    private static final List<ColorReset> COLORS = List.of(
        new ColorReset("clickGuiColor", Runtime.DEFAULT_CLICK_GUI_COLOR, Runtime::setClickGuiColor),
        new ColorReset("clickGuiColor2", Runtime.DEFAULT_CLICK_GUI_COLOR_2, Runtime::setClickGuiColor2),
        new ColorReset("blockOutlineColor1", Runtime.DEFAULT_GRADIENT_COLOR_1, Runtime::setBlockOutlineColor1),
        new ColorReset("blockOutlineColor2", Runtime.DEFAULT_GRADIENT_COLOR_2, Runtime::setBlockOutlineColor2),
        new ColorReset("blockOutlineFilledColor", Runtime.DEFAULT_BLOCK_OUTLINE_FILLED_COLOR, Runtime::setBlockOutlineFilledColor),
        new ColorReset("customTooltipBorderColor1", Runtime.DEFAULT_GRADIENT_COLOR_1, Runtime::setCustomTooltipBorderColor1),
        new ColorReset("customTooltipBorderColor2", Runtime.DEFAULT_GRADIENT_COLOR_2, Runtime::setCustomTooltipBorderColor2),
        new ColorReset("customTooltipBackgroundColor", Runtime.DEFAULT_CUSTOM_TOOLTIP_BACKGROUND_COLOR, Runtime::setCustomTooltipBackgroundColor)
    );

    private ColorResetRegistry() {}

    public static void resetAll() {
        for (ColorReset color : COLORS) color.reset();
    }

    private record ColorReset(String configKey, int defaultColor, IntConsumer setter) {
        private void reset() {
            setter.accept(defaultColor);
            ConfigManager.setInt(configKey, defaultColor);
        }
    }
}
