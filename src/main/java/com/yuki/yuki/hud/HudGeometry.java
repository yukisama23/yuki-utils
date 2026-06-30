package com.yuki.yuki.hud;

public final class HudGeometry {
    private HudGeometry() {}

    public record Bounds(int x, int y, int width, int height, int scaledWidth, int scaledHeight, float scale) {}

    public static Bounds editorBounds(HudElement element, int screenWidth, int screenHeight) {
        return bounds(element, screenWidth, screenHeight, true);
    }

    public static Bounds bounds(HudElement element, int screenWidth, int screenHeight, boolean editor) {
        float scale = safeScale(element.getScale());
        int width = Math.max(1, editor ? element.getEditorWidth() : element.getLastWidth());
        int height = Math.max(1, editor ? element.getEditorHeight() : element.getLastHeight());
        int x = element.getX(screenWidth, width, scale);
        int y = element.getY(screenHeight, height, scale);
        return new Bounds(x, y, width, height, scaled(width, scale), scaled(height, scale), scale);
    }

    public static float safeScale(float scale) {
        return scale <= 0.0f ? 1.0f : scale;
    }

    public static int scaled(int value, float scale) {
        return Math.max(1, Math.round(Math.max(1, value) * safeScale(scale)));
    }

    public static int centerX(int screenWidth, int elementWidth, float scale) {
        int w = scaled(elementWidth, scale);
        return Math.max(0, (screenWidth - w) / 2);
    }

    public static int centerY(int screenHeight, int elementHeight, float scale) {
        int h = scaled(elementHeight, scale);
        return Math.max(0, (screenHeight - h) / 2);
    }

    public static int anchorX(int screenWidth, int elementWidth, float scale, int x) {
        if (x < 0) return centerX(screenWidth, elementWidth, scale);
        int w = scaled(elementWidth, scale);
        return Math.max(0, Math.min(screenWidth - w, x - w / 2));
    }

    public static int anchorY(int screenHeight, int elementHeight, float scale, int y) {
        if (y < 0) return centerY(screenHeight, elementHeight, scale);
        int h = scaled(elementHeight, scale);
        return Math.max(0, Math.min(screenHeight - h, y - h / 2));
    }
}
