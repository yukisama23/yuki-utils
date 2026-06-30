package com.yuki.yuki.hud;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.util.GradientTextRenderer;
import com.yuki.yuki.util.HitboxDistance;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;

import java.util.Locale;

public final class ReachDisplayHudElement implements HudElement {
    private static final String LABEL = "reach";
    private static final int GRAY = 0xFFAAAAAA;
    private static final long DISPLAY_MILLIS = 3000L;
    private static final double VANILLA_ENTITY_INTERACTION_RANGE = 3.0D;
    private static final double RANGE_EPSILON = 0.0001D;

    private static double lastReach = -1.0D;
    private static long lastAttackTimeMs = Long.MIN_VALUE;

    private int lastW = 1;
    private int lastH = 1;

    public static void recordAttack(PlayerEntity player, Entity target) {
        if (player == null || target == null || target == player || target.isRemoved()) return;
        double distanceSq = HitboxDistance.eyeToEntitySq(player, target);
        if (!Double.isFinite(distanceSq)) return;
        double range = VANILLA_ENTITY_INTERACTION_RANGE + RANGE_EPSILON;
        if (distanceSq > range * range) return;
        lastReach = Math.sqrt(Math.max(0.0D, distanceSq));
        lastAttackTimeMs = Util.getMeasuringTimeMs();
    }

    @Override
    public String getName() {
        return "reach display";
    }

    @Override
    public boolean isEnabled() {
        return Runtime.isReachDisplayHudEnabled();
    }

    @Override
    public int getX(int screenWidth, int elementWidth, float scale) {
        return anchorX(screenWidth, elementWidth, scale, Runtime.getReachDisplayHudX());
    }

    @Override
    public int getY(int screenHeight, int elementHeight, float scale) {
        return anchorY(screenHeight, elementHeight, scale, Runtime.getReachDisplayHudY());
    }

    @Override
    public float getScale() {
        return Runtime.getReachDisplayHudScale();
    }

    @Override
    public void setX(int x) {
        Runtime.setReachDisplayHudX(x);
    }

    @Override
    public void setY(int y) {
        Runtime.setReachDisplayHudY(y);
    }

    @Override
    public void setScale(float scale) {
        Runtime.setReachDisplayHudScale(scale);
    }

    @Override
    public void resetToDefault() {
        Runtime.setReachDisplayHudX(-1);
        Runtime.setReachDisplayHudY(-1);
        Runtime.setReachDisplayHudScale(1.0f);
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
        if (client == null || client.textRenderer == null || !shouldRender(client, editor)) return;
        TextRenderer tr = client.textRenderer;
        String suffix = suffix(editor);
        updateDimensions(tr, suffix);

        var matrices = ctx.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(scale, scale);
        if (editor) ctx.fill(-1, -1, lastW + 1, lastH + 1, 0x40FFFFFF);
        GradientTextRenderer.drawTwoColorShadow(ctx, tr, LABEL, 0, 0, Runtime.getClickGuiColor(), Runtime.getClickGuiColor2());
        ctx.drawText(tr, suffix, tr.getWidth(LABEL), 0, GRAY, true);
        matrices.popMatrix();
    }

    @Override
    public void tick(MinecraftClient client) {
        if (client == null || client.player == null) {
            clearLastReach();
            return;
        }
        if (lastAttackTimeMs != Long.MIN_VALUE && Util.getMeasuringTimeMs() - lastAttackTimeMs > DISPLAY_MILLIS) {
            clearLastReach();
        }
    }

    private boolean shouldRender(MinecraftClient client, boolean editor) {
        if (editor) return true;
        if (client == null || client.player == null || lastReach < 0.0D || lastAttackTimeMs == Long.MIN_VALUE) return false;
        long ageMs = Util.getMeasuringTimeMs() - lastAttackTimeMs;
        if (ageMs < 0L || ageMs > DISPLAY_MILLIS) {
            clearLastReach();
            return false;
        }
        return true;
    }

    private static void clearLastReach() {
        lastReach = -1.0D;
        lastAttackTimeMs = Long.MIN_VALUE;
    }

    private String suffix(boolean editor) {
        double reach = editor || lastReach < 0.0D ? 3.00D : lastReach;
        return " " + String.format(Locale.ROOT, "%.2f", reach);
    }

    private void updateLastDimensions(boolean editor) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null || !shouldRender(client, editor)) return;
        updateDimensions(client.textRenderer, suffix(editor));
    }

    private void updateDimensions(TextRenderer tr, String suffix) {
        lastW = Math.max(1, tr.getWidth(LABEL) + tr.getWidth(suffix));
        lastH = Math.max(1, tr.fontHeight);
    }
}
