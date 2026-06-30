package com.yuki.yuki.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public final class ShulkerTooltipComponent implements TooltipComponent {
    private final ShulkerTooltipPreview preview;

    public ShulkerTooltipComponent(ShulkerTooltipPreview preview) {
        this.preview = preview;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return ShulkerTooltipPreview.WIDTH;
    }

    @Override
    public int getHeight(TextRenderer textRenderer) {
        return ShulkerTooltipPreview.HEIGHT;
    }

    @Override
    public void drawText(DrawContext context, TextRenderer textRenderer, int x, int y) {}

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        ShulkerTooltipRenderer.drawItems(context, textRenderer, preview, x, y);
    }
}
