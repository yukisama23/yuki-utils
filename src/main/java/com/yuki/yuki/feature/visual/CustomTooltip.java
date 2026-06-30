package com.yuki.yuki.feature.visual;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.render.CustomTooltipRoundRectRenderer;
import com.yuki.yuki.util.RoundedRectMath;
import com.yuki.yuki.util.UiMath;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import org.joml.Vector2ic;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public final class CustomTooltip {
    private static final List<QueuedTooltip> QUEUED_TOOLTIPS = new ArrayList<>();
    private static boolean renderingQueuedTooltips;

    private CustomTooltip() {}

    public static void queue(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, boolean force) {
        if ((!Runtime.isCustomTooltipEnabled() && !force) || components == null || components.isEmpty() || positioner == null) return;
        QUEUED_TOOLTIPS.add(new QueuedTooltip(textRenderer, new ArrayList<>(components), x, y, positioner, force));
    }

    public static void renderQueued(DrawContext context) {
        if (renderingQueuedTooltips) return;
        if (QUEUED_TOOLTIPS.isEmpty()) return;
        boolean customTooltip = Runtime.isCustomTooltipEnabled();
        if (!customTooltip && !hasForcedTooltip()) {
            QUEUED_TOOLTIPS.clear();
            return;
        }
        List<QueuedTooltip> tooltips = new ArrayList<>(QUEUED_TOOLTIPS);
        QUEUED_TOOLTIPS.clear();
        renderingQueuedTooltips = true;
        try {
            for (QueuedTooltip tooltip : tooltips) {
                if (customTooltip || tooltip.force()) {
                    renderNow(context, tooltip.textRenderer(), tooltip.components(), tooltip.x(), tooltip.y(), tooltip.positioner());
                }
            }
            context.drawDeferredElements();
        } finally {
            renderingQueuedTooltips = false;
        }
    }

    private static boolean hasForcedTooltip() {
        for (QueuedTooltip tooltip : QUEUED_TOOLTIPS) {
            if (tooltip.force()) return true;
        }
        return false;
    }

    private static void renderNow(DrawContext context, TextRenderer textRenderer, List<TooltipComponent> rawComponents, int x, int y, TooltipPositioner positioner) {
        List<TooltipComponent> components = rawComponents;
        boolean single = components.size() == 1;
        int width = 0;
        int height = single ? -2 : 0;
        for (TooltipComponent component : components) {
            width = Math.max(width, component.getWidth(textRenderer));
            height += component.getHeight(textRenderer);
        }
        Vector2ic pos = positioner.getPosition(context.getScaledWindowWidth(), context.getScaledWindowHeight(), x, y, width, height);
        renderCombined(context, textRenderer, components, pos.x(), pos.y(), width, height);
    }

    private static void renderCombined(DrawContext context, TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, int width, int height) {
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        int horizontalMargin = 4;
        int verticalMargin = 20;
        int boxWidth = Math.max(0, Math.min(width + 8, screenWidth - horizontalMargin * 2));
        int boxHeight = Math.max(0, Math.min(height + 8, screenHeight - verticalMargin * 2));
        int maxX = Math.max(horizontalMargin, screenWidth - horizontalMargin - boxWidth);
        int maxY = Math.max(verticalMargin, screenHeight - verticalMargin - boxHeight);
        int boxX = UiMath.clamp(x - 4, horizontalMargin, maxX);
        int boxY = height + 8 < screenHeight - verticalMargin * 2 ? UiMath.clamp(y - 4, verticalMargin, maxY) : verticalMargin;
        int textX = boxX + 4;
        int textY = boxY + 4;
        drawBox(context, boxX, boxY, boxWidth, boxHeight);
        drawComponents(context, textRenderer, components, textX, boxX, boxY, boxWidth, boxHeight, textY, width, height, true);
        drawFade(context, boxX, boxY, boxWidth, boxHeight, height);
    }

    private static void drawComponents(DrawContext context, TextRenderer textRenderer, List<TooltipComponent> components, int textX, int boxX, int boxY, int boxWidth, int boxHeight, int startY, int width, int totalHeight, boolean combined) {
        context.enableScissor(boxX, boxY, boxX + boxWidth, boxY + boxHeight);
        int drawY = startY;
        for (int i = 0; i < components.size(); i++) {
            TooltipComponent component = components.get(i);
            component.drawText(context, textRenderer, textX, drawY);
            drawY += component.getHeight(textRenderer) + (combined && i == 0 ? 2 : 0);
        }
        drawY = startY;
        for (int i = 0; i < components.size(); i++) {
            TooltipComponent component = components.get(i);
            component.drawItems(textRenderer, textX, drawY, width, totalHeight, context);
            drawY += component.getHeight(textRenderer) + (combined && i == 0 ? 2 : 0);
        }
        context.disableScissor();
    }

    private static void drawBox(DrawContext context, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) return;
        int radius = Math.min(Runtime.getCustomTooltipRoundness(), Math.min(width, height) / 2);
        CustomTooltipRoundRectRenderer.drawFill(context, x, y, width, height, radius, Runtime.getCustomTooltipBackgroundColorWithAlpha());
        float borderWidth = Runtime.getCustomTooltipBorderWidth();
        if (Runtime.isCustomTooltipBorderEnabled() && borderWidth > 0.0f) CustomTooltipRoundRectRenderer.drawBorder(context, x, y, width, height, radius, borderWidth, Runtime.getCustomTooltipBorderColor1(), Runtime.getCustomTooltipBorderColor2());
    }

    private static void drawFade(DrawContext context, int x, int y, int width, int height, int contentHeight) {
        if (contentHeight <= height || width <= 0 || height <= 0) return;
        int background = Runtime.getCustomTooltipBackgroundColorWithAlpha();
        int alpha = (background >>> 24) & 255;
        int rgb = background & 0x00FFFFFF;
        int radius = Math.min(Runtime.getCustomTooltipRoundness(), Math.min(width, height) / 2);
        int fadeHeight = Math.min(18, height);
        int startRow = Math.max(0, height - fadeHeight);
        for (int row = startRow; row < height; row++) {
            float t = fadeHeight <= 1 ? 1.0f : (row - startRow + 1) / (float) fadeHeight;
            int rowAlpha = MathHelper.clamp(Math.round(alpha * t), 0, alpha);
            if (rowAlpha <= 0) continue;
            int inset = RoundedRectMath.inset(row, height, radius);
            if (width - inset * 2 <= 0) continue;
            context.fill(x + inset, y + row, x + width - inset, y + row + 1, (rowAlpha << 24) | rgb);
        }
    }

    private record QueuedTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, boolean force) {}
}
