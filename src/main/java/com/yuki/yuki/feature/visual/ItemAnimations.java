package com.yuki.yuki.feature.visual;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Hand;

public final class ItemAnimations {
    private static boolean swinging = false;
    private static int swingTimeTick = 0;
    private static float attackAnim = 0.0f;
    private static float prevAttackAnim = 0.0f;
    private static Hand activeHand = Hand.MAIN_HAND;

    private ItemAnimations() {}

    public static boolean isEnabled() {
        return Runtime.isItemAnimationsEnabled();
    }

    private static float getItemScale() {
        return (float) Math.pow(2.0, Runtime.getItemAnimationsScale() * 2.0f);
    }

    private static double getSwingSpeed() {
        return Math.pow(2.0, Runtime.getItemAnimationsSwingSpeed() * 2.0f);
    }

    public static void applyScale(MatrixStack matrices) {
        if (!isEnabled()) return;
        float scale = getItemScale();
        if (scale != 1.0f) matrices.scale(scale, scale, scale);
    }

    private static float getActualSwingAnimation(float pt) {
        float d = attackAnim - prevAttackAnim;
        if (d < 0.0f) d += 1.0f;
        return prevAttackAnim + d * pt;
    }

    public static float getSwingAnimation(float tickDelta) {
        if (!isEnabled()) return 0.0f;
        return getActualSwingAnimation(tickDelta);
    }

    public static float getPreviousSwingAnimation() {
        if (!isEnabled()) return 0.0f;
        return prevAttackAnim;
    }

    public static float getCurrentSwingAnimation() {
        if (!isEnabled()) return 0.0f;
        return attackAnim;
    }

    public static boolean shouldOverrideThirdPersonSwing() {
        return isEnabled() && Runtime.isItemAnimationsIgnoreEffectsEnabled();
    }

    public static boolean shouldAnimateHand(Hand hand) {
        return isEnabled() && (hand == null || hand == activeHand);
    }

    public static int getThirdPersonSwingDuration() {
        double duration = 6.0 / getSwingSpeed();
        return Math.max(1, (int) Math.round(duration));
    }

    private static int getCurrentSwingDuration() {
        if (!isEnabled()) return 6;
        if (Runtime.isItemAnimationsIgnoreEffectsEnabled()) return 6;

        int base = 6;
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return base;

        if (player.hasStatusEffect(StatusEffects.HASTE) || player.hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
            int amp = 0;
            if (player.hasStatusEffect(StatusEffects.HASTE)) {
                StatusEffectInstance e = player.getStatusEffect(StatusEffects.HASTE);
                amp = e == null ? 0 : e.getAmplifier();
            }
            base = 6 - (1 + amp);
        } else if (player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            StatusEffectInstance e = player.getStatusEffect(StatusEffects.MINING_FATIGUE);
            int amp = e == null ? 0 : e.getAmplifier();
            base = 6 + (1 + amp) * 2;
        }

        if (base < 1) base = 1;
        return base;
    }

    private static double getSwingTime() {
        return swingTimeTick * getSwingSpeed();
    }

    public static void onSwing(Hand hand) {
        if (!isEnabled()) return;
        activeHand = hand == null ? Hand.MAIN_HAND : hand;

        int total = getCurrentSwingDuration();
        if (swinging && swingTimeTick >= 0 && getSwingTime() < total / 2.0) return;

        swingTimeTick = -1;
        swinging = true;
    }

    public static void onUpdateSwingTime() {
        if (!isEnabled()) return;

        prevAttackAnim = attackAnim;
        int total = getCurrentSwingDuration();

        if (swinging) {
            swingTimeTick++;
            if (getSwingTime() >= total) {
                swingTimeTick = 0;
                swinging = false;
            }
        } else {
            swingTimeTick = 0;
        }

        attackAnim = (float) (getSwingTime() / total);
    }
}
