package com.yuki.yuki.hud;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.feature.hud.TotemTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class TotemTweaksHudElement implements HudElement {
    private static final int ICON_SIZE = 16;
    private static final ItemStack TOTEM_STACK = new ItemStack(Items.TOTEM_OF_UNDYING);

    @Override
    public String getName() {
        return "totem count";
    }

    @Override
    public boolean isEnabled() {
        return Runtime.isTotemTweaksEnabled() && Runtime.isTotemTweaksTotemCountEnabled();
    }

    @Override
    public int getX(int screenWidth, int elementWidth, float scale) {
        return anchorX(screenWidth, elementWidth, scale, Runtime.getTotemTweaksHudX());
    }

    @Override
    public int getY(int screenHeight, int elementHeight, float scale) {
        return anchorY(screenHeight, elementHeight, scale, Runtime.getTotemTweaksHudY());
    }

    @Override
    public float getScale() {
        return Runtime.getTotemTweaksHudScale();
    }

    @Override
    public void setX(int x) {
        Runtime.setTotemTweaksHudX(x);
    }

    @Override
    public void setY(int y) {
        Runtime.setTotemTweaksHudY(y);
    }

    @Override
    public void setScale(float scale) {
        Runtime.setTotemTweaksHudScale(scale);
    }

    @Override
    public void resetToDefault() {
        Runtime.setTotemTweaksHudX(-1);
        Runtime.setTotemTweaksHudY(-1);
        Runtime.setTotemTweaksHudScale(1.0f);
    }

    @Override
    public int getLastWidth() {
        return Math.max(ICON_SIZE, getCountTextWidth(false));
    }

    @Override
    public int getLastHeight() {
        return ICON_SIZE + 1 + getTextHeight();
    }

    @Override
    public int getEditorWidth() {
        return Math.max(ICON_SIZE, getCountTextWidth(true));
    }

    @Override
    public int getEditorHeight() {
        return ICON_SIZE + 1 + getTextHeight();
    }

    @Override
    public void render(DrawContext ctx, int x, int y, float scale, boolean editor) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) return;

        TextRenderer textRenderer = client.textRenderer;
        int totemCount = editor ? 64 : TotemTweaks.countTotems(client);
        if (!editor && totemCount <= 0) return;
        String countText = String.valueOf(totemCount);

        var matrices = ctx.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(scale, scale);

        int elementWidth = Math.max(ICON_SIZE, textRenderer.getWidth(countText));
        int elementHeight = ICON_SIZE + 1 + textRenderer.fontHeight;
        if (editor) {
            ctx.fill(-1, -1, elementWidth + 1, elementHeight + 1, 0x40FFFFFF);
        }

        int iconX = (elementWidth - ICON_SIZE) / 2;
        ctx.drawItem(TOTEM_STACK, iconX, 0);

        int textX = (elementWidth - textRenderer.getWidth(countText)) / 2;
        int textY = ICON_SIZE + 1;
        ctx.drawText(textRenderer, countText, textX, textY, TotemTweaks.colorForTotemCount(totemCount), true);

        matrices.popMatrix();
    }

    @Override
    public void tick(MinecraftClient client) {}

    private int getCountTextWidth(boolean editor) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) return ICON_SIZE;
        return client.textRenderer.getWidth(String.valueOf(editor ? 64 : TotemTweaks.countTotems(client)));
    }

    private int getTextHeight() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) return 9;
        return client.textRenderer.fontHeight;
    }
}
