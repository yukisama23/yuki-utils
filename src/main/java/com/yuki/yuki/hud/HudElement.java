package com.yuki.yuki.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public interface HudElement {
    String getName();
    boolean isEnabled();
    default int getX(int screenWidth, int elementWidth, float scale) {
        return centerX(screenWidth, elementWidth, scale);
    }
    default int getY(int screenHeight, int elementHeight, float scale) {
        return centerY(screenHeight, elementHeight, scale);
    }
    default int centerX(int screenWidth, int elementWidth, float scale) {
        return HudGeometry.centerX(screenWidth, elementWidth, scale);
    }
    default int centerY(int screenHeight, int elementHeight, float scale) {
        return HudGeometry.centerY(screenHeight, elementHeight, scale);
    }
    default int anchorX(int screenWidth, int elementWidth, float scale, int x) {
        return HudGeometry.anchorX(screenWidth, elementWidth, scale, x);
    }
    default int anchorY(int screenHeight, int elementHeight, float scale, int y) {
        return HudGeometry.anchorY(screenHeight, elementHeight, scale, y);
    }
    default void setPositionFromTopLeft(int x, int y, int width, int height) {
        setX(x + Math.max(1, width) / 2);
        setY(y + Math.max(1, height) / 2);
    }
    float getScale();
    void setX(int x);
    void setY(int y);
    void setScale(float scale);
    default void resetToDefault() {}
    int getLastWidth();
    int getLastHeight();
    default int getEditorWidth() {
        return getLastWidth();
    }
    default int getEditorHeight() {
        return getLastHeight();
    }
    void render(DrawContext ctx, int x, int y, float scale, boolean editor);
    void tick(MinecraftClient client);
}
