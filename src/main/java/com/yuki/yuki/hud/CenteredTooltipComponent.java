package com.yuki.yuki.hud;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.util.GradientTextRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public final class CenteredTooltipComponent implements TooltipComponent {
    private final TooltipComponent delegate;
    private final int targetWidth;
    private final String gradientText;

    public CenteredTooltipComponent(TooltipComponent delegate, int targetWidth, String gradientText) {
        this.delegate = delegate;
        this.targetWidth = Math.max(0, targetWidth);
        this.gradientText = gradientText == null || gradientText.isBlank() ? null : gradientText;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return Math.max(targetWidth, contentWidth(textRenderer));
    }

    @Override
    public int getHeight(TextRenderer textRenderer) {
        return delegate.getHeight(textRenderer);
    }

    @Override
    public void drawText(DrawContext context, TextRenderer textRenderer, int x, int y) {
        int drawX = centeredX(textRenderer, x);
        if (gradientText == null) {
            delegate.drawText(context, textRenderer, drawX, y);
            return;
        }
        GradientTextRenderer.drawTwoColorShadow(context, textRenderer, gradientText, drawX, y, Runtime.getClickGuiColor(), Runtime.getClickGuiColor2());
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
        if (gradientText == null) {
            delegate.drawItems(textRenderer, centeredX(textRenderer, x), y, width, height, context);
        }
    }

    private int centeredX(TextRenderer textRenderer, int x) {
        int contentWidth = contentWidth(textRenderer);
        int availableWidth = Math.max(targetWidth, contentWidth);
        return x + Math.max(0, (availableWidth - contentWidth) / 2);
    }

    private int contentWidth(TextRenderer textRenderer) {
        return gradientText == null ? delegate.getWidth(textRenderer) : GradientTextRenderer.strippedWidth(textRenderer, gradientText);
    }
}
