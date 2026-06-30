package com.yuki.yuki.hud;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class ArmorHudOffhandElement implements HudElement {
    @Override
    public String getName() {
        return "offhand slot";
    }

    @Override
    public boolean isEnabled() {
        return Runtime.isArmorHudCustomPosition() && Runtime.isArmorHudOffhandEnabled();
    }

    @Override
    public int getX(int screenWidth, int elementWidth, float scale) {
        return anchorX(screenWidth, elementWidth, scale, Runtime.getArmorHudOffhandX());
    }

    @Override
    public int getY(int screenHeight, int elementHeight, float scale) {
        return anchorY(screenHeight, elementHeight, scale, Runtime.getArmorHudOffhandY());
    }

    @Override
    public float getScale() {
        return Runtime.getArmorHudOffhandScale();
    }

    @Override
    public void setX(int x) {
        Runtime.setArmorHudOffhandX(x);
    }

    @Override
    public void setY(int y) {
        Runtime.setArmorHudOffhandY(y);
    }

    @Override
    public void setScale(float scale) {
        Runtime.setArmorHudOffhandScale(scale);
    }

    @Override
    public void resetToDefault() {
        Runtime.setArmorHudOffhandX(-1);
        Runtime.setArmorHudOffhandY(-1);
        Runtime.setArmorHudOffhandScale(1.0f);
    }

    @Override
    public int getLastWidth() {
        return ArmorHudElement.SLOT_SIZE;
    }

    @Override
    public int getLastHeight() {
        return ArmorHudElement.SLOT_SIZE;
    }

    @Override
    public void render(DrawContext ctx, int x, int y, float scale, boolean editor) {
        ArmorHudOffhandRenderer.renderCustom(ctx, x, y, scale, editor);
    }

    @Override
    public void tick(MinecraftClient client) {}
}
