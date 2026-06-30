package com.yuki.yuki.clickgui;

import net.minecraft.client.gui.DrawContext;

public record GuiClip(int left, int top, int right, int bottom) {
    public static GuiClip bounds(int left, int top, int right, int bottom) {
        return new GuiClip(left, top, right, bottom);
    }

    public GuiClip intersect(GuiClip other) {
        return new GuiClip(Math.max(left, other.left), Math.max(top, other.top), Math.min(right, other.right), Math.min(bottom, other.bottom));
    }

    public boolean isEmpty() {
        return right <= left || bottom <= top;
    }

    public boolean intersectsY(int y, int height) {
        return y < bottom && y + height > top;
    }

    public void enable(DrawContext context) {
        context.enableScissor(left, top, right, bottom);
    }
}
