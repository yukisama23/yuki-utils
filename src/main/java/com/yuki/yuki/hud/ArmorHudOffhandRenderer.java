package com.yuki.yuki.hud;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;

public final class ArmorHudOffhandRenderer {
    static final int RESERVED_WIDTH = 29;
    private static final int HOTBAR_HALF_WIDTH = 91;
    private static final int HOTBAR_SIDE_GAP = 7;
    private static final int ITEM_OFFSET = 3;
    private static final ItemStack EDITOR_STACK = new ItemStack(Items.TOTEM_OF_UNDYING);

    private ArmorHudOffhandRenderer() {}

    public static void renderFixed(DrawContext ctx, int screenWidth, int screenHeight) {
        if (!Runtime.shouldRenderFixedOffhandHud()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        ItemStack stack = client.player.getOffHandStack();
        if (ArmorHudElement.isEmpty(stack)) return;
        boolean right = isOffhandOnRight(client.player);
        renderSlot(ctx, stack, slotX(screenWidth, right), screenHeight - ArmorHudElement.SLOT_SIZE, 1.0f, false);
    }

    public static void renderCustom(DrawContext ctx, int x, int y, float scale, boolean editor) {
        if (!Runtime.isArmorHudOffhandEnabled()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        ItemStack stack = editor ? EDITOR_STACK : client != null && client.player != null ? client.player.getOffHandStack() : ItemStack.EMPTY;
        if (ArmorHudElement.isEmpty(stack)) return;
        renderSlot(ctx, stack, x, y, scale, editor);
    }

    private static void renderSlot(DrawContext ctx, ItemStack stack, int x, int y, float scale, boolean editor) {
        MinecraftClient client = MinecraftClient.getInstance();
        var matrices = ctx.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(scale, scale);
        if (Runtime.isArmorHudOffhandHotbarStyle()) {
            ArmorHudElement.drawHotbarBackground(ctx, ArmorHudElement.SLOT_SIZE);
        }
        ctx.drawItem(stack, ITEM_OFFSET, ArmorHudElement.ITEM_OFFSET);
        ArmorHudElement.drawDurabilityBar(ctx, stack, ITEM_OFFSET, ArmorHudElement.ITEM_OFFSET);
        if (!editor && client != null) {
            drawStackCount(ctx, client.textRenderer, stack, ITEM_OFFSET, ArmorHudElement.ITEM_OFFSET);
        }
        matrices.popMatrix();
    }

    private static void drawStackCount(DrawContext ctx, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        if (textRenderer == null || stack == null || stack.isEmpty() || stack.getCount() <= 1) return;
        String count = String.valueOf(stack.getCount());
        int textX = x + 19 - 2 - textRenderer.getWidth(count);
        int textY = y + 6 + 3;
        ctx.drawText(textRenderer, count, textX, textY, 0xFFFFFFFF, true);
    }

    static boolean isOffhandOnRight(PlayerEntity player) {
        return player.getMainArm() == Arm.LEFT;
    }

    private static int slotX(int screenWidth, boolean right) {
        int centerX = screenWidth / 2;
        if (right) return centerX + HOTBAR_HALF_WIDTH + HOTBAR_SIDE_GAP;
        return centerX - HOTBAR_HALF_WIDTH - HOTBAR_SIDE_GAP - ArmorHudElement.SLOT_SIZE;
    }
}
