package com.yuki.yuki.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

public final class ShulkerTooltipRenderer {
    private ShulkerTooltipRenderer() {}

    public static void drawItems(DrawContext context, TextRenderer textRenderer, ShulkerTooltipPreview preview, int x, int y) {
        if (preview == null || preview.isEmpty()) return;
        int top = y + ShulkerTooltipPreview.PADDING_TOP;
        for (int slot = 0; slot < ShulkerTooltipPreview.SLOT_COUNT; slot++) {
            ItemStack stack = slot < preview.stacks().size() ? preview.stacks().get(slot) : ItemStack.EMPTY;
            if (stack == null || stack.isEmpty()) continue;

            int col = slot % ShulkerTooltipPreview.COLUMNS;
            int row = slot / ShulkerTooltipPreview.COLUMNS;
            int itemX = x + col * ShulkerTooltipPreview.SLOT_SIZE + 1;
            int itemY = top + row * ShulkerTooltipPreview.SLOT_SIZE + 1;
            context.drawItem(stack, itemX, itemY);
            context.drawStackOverlay(textRenderer, stack, itemX, itemY);
        }
    }
}
