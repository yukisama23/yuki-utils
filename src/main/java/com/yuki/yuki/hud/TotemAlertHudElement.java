package com.yuki.yuki.hud;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.MinecraftClient;

public final class TotemAlertHudElement extends TextHudElement {
    private static final int SHOW_TICKS = 20;
    private static final int TEXT_COLOR = 0xFFFFFF55;
    private static int remainingTicks;
    private static int shownTotemCount;

    public static void show(int totemCount) {
        shownTotemCount = Math.max(0, totemCount);
        remainingTicks = SHOW_TICKS;
    }

    @Override
    public String getName() {
        return "totem alert";
    }

    @Override
    public boolean isEnabled() {
        return Runtime.isTotemTweaksEnabled() && Runtime.isTotemTweaksTotemAlertEnabled();
    }

    @Override
    protected int storedX() {
        return Runtime.getTotemTweaksAlertHudX();
    }

    @Override
    protected int storedY() {
        return Runtime.getTotemTweaksAlertHudY();
    }

    @Override
    protected float storedScale() {
        return Runtime.getTotemTweaksAlertHudScale();
    }

    @Override
    protected void storeX(int x) {
        Runtime.setTotemTweaksAlertHudX(x);
    }

    @Override
    protected void storeY(int y) {
        Runtime.setTotemTweaksAlertHudY(y);
    }

    @Override
    protected void storeScale(float scale) {
        Runtime.setTotemTweaksAlertHudScale(scale);
    }

    @Override
    protected boolean shouldRender(boolean editor) {
        return editor || remainingTicks > 0;
    }

    @Override
    protected int textColor(boolean editor) {
        return TEXT_COLOR;
    }

    @Override
    protected String text(MinecraftClient client, boolean editor) {
        return (editor ? 64 : shownTotemCount) + " totem";
    }

    @Override
    public void tick(MinecraftClient client) {
        if (remainingTicks > 0) remainingTicks--;
    }
}
