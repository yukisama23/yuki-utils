package com.yuki.yuki.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public final class TooltipSpacerComponent implements TooltipComponent {
    public static final TooltipSpacerComponent LINE = new TooltipSpacerComponent();

    private TooltipSpacerComponent() {}

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 0;
    }

    @Override
    public int getHeight(TextRenderer textRenderer) {
        return textRenderer.fontHeight + 1;
    }

    @Override
    public void drawText(DrawContext context, TextRenderer textRenderer, int x, int y) {}

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {}
}
