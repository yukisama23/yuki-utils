package com.yuki.yuki.clickgui;

import com.yuki.yuki.util.UiMath;

public final class GuiAnimation {
    public static final float DEFAULT_SPEED = 7.0f;

    private final float speed;
    private boolean open;
    private float progress;

    public GuiAnimation(boolean open) {
        this(open, DEFAULT_SPEED);
    }

    public GuiAnimation(boolean open, float speed) {
        this.open = open;
        this.progress = open ? 1.0f : 0.0f;
        this.speed = Math.max(0.0f, speed);
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isVisible() {
        return open || progress > 0.0f;
    }

    public void update(float seconds) {
        float target = open ? 1.0f : 0.0f;
        float step = Math.max(0.0f, seconds) * speed;
        if (progress < target) {
            progress = Math.min(target, progress + step);
        } else if (progress > target) {
            progress = Math.max(target, progress - step);
        }
    }

    public int animatedHeight(int fullHeight) {
        if (fullHeight <= 0 || progress <= 0.0f) return 0;
        return Math.max(1, Math.round(fullHeight * easedProgress()));
    }

    public float easedProgress() {
        float value = UiMath.clamp01(progress);
        float inv = 1.0f - value;
        return 1.0f - inv * inv * inv;
    }
}
