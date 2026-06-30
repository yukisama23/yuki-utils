package com.yuki.yuki.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public abstract class TextHudElement implements HudElement {
    private int lastW = 1;
    private int lastH = 1;

    protected abstract int storedX();
    protected abstract int storedY();
    protected abstract float storedScale();
    protected abstract void storeX(int x);
    protected abstract void storeY(int y);
    protected abstract void storeScale(float scale);
    protected abstract String text(MinecraftClient client, boolean editor);

    @Override
    public int getX(int screenWidth, int elementWidth, float scale) {
        return anchorX(screenWidth, elementWidth, scale, storedX());
    }

    @Override
    public int getY(int screenHeight, int elementHeight, float scale) {
        return anchorY(screenHeight, elementHeight, scale, storedY());
    }

    @Override
    public float getScale() {
        return storedScale();
    }

    @Override
    public void setX(int x) {
        storeX(x);
    }

    @Override
    public void setY(int y) {
        storeY(y);
    }

    @Override
    public void setScale(float scale) {
        storeScale(scale);
    }

    @Override
    public void resetToDefault() {
        storeX(-1);
        storeY(-1);
        storeScale(1.0f);
    }

    @Override
    public int getLastWidth() {
        updateLastDimensions(false);
        return lastW;
    }

    @Override
    public int getLastHeight() {
        updateLastDimensions(false);
        return lastH;
    }

    @Override
    public int getEditorWidth() {
        updateLastDimensions(true);
        return lastW;
    }

    @Override
    public int getEditorHeight() {
        updateLastDimensions(true);
        return lastH;
    }

    @Override
    public void render(DrawContext ctx, int x, int y, float scale, boolean editor) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null || !shouldRender(editor)) return;
        TextRenderer tr = client.textRenderer;
        String text = safeText(client, editor);
        if (text.isEmpty()) {
            setLastDimensions(1, 1);
            return;
        }
        setLastDimensions(measureWidth(tr, text), measureHeight(tr, text));
        var matrices = ctx.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(scale, scale);
        if (editor) ctx.fill(-1, -1, lastW + 1, lastH + 1, 0x40FFFFFF);
        drawText(ctx, tr, text, textX(), textY(), editor);
        matrices.popMatrix();
    }

    @Override
    public void tick(MinecraftClient client) {}

    protected boolean shouldRender(boolean editor) {
        return true;
    }

    protected int textX() {
        return 0;
    }

    protected int textY() {
        return 0;
    }

    protected int textColor(boolean editor) {
        return 0xFFFFFFFF;
    }

    protected void drawText(DrawContext ctx, TextRenderer tr, String text, int x, int y, boolean editor) {
        ctx.drawText(tr, text, x, y, textColor(editor), true);
    }

    protected final void setLastDimensions(int width, int height) {
        lastW = Math.max(1, width);
        lastH = Math.max(1, height);
    }

    private void updateLastDimensions(boolean editor) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null || !shouldRender(editor)) return;
        TextRenderer tr = client.textRenderer;
        String text = safeText(client, editor);
        if (text.isEmpty()) {
            setLastDimensions(1, 1);
            return;
        }
        setLastDimensions(measureWidth(tr, text), measureHeight(tr, text));
    }

    private int measureWidth(TextRenderer tr, String text) {
        return textX() + tr.getWidth(text);
    }

    private int measureHeight(TextRenderer tr, String text) {
        return textY() + tr.fontHeight;
    }

    private String safeText(MinecraftClient client, boolean editor) {
        String text = text(client, editor);
        return text == null ? "" : text;
    }
}
