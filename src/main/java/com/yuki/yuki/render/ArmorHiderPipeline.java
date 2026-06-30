package com.yuki.yuki.render;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.util.ItemStackUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public final class ArmorHiderPipeline {
    private ArmorHiderPipeline() {}

    public static boolean shouldCancelRender() {
        if (!Runtime.isArmorHiderEnabled()) return false;
        if (!Runtime.isArmorHiderSkullEnabled()
                && !Runtime.isArmorHiderHelmetEnabled()
                && !Runtime.isArmorHiderChestplateEnabled()
                && !Runtime.isArmorHiderElytraEnabled()
                && !Runtime.isArmorHiderLeggingsEnabled()
                && !Runtime.isArmorHiderBootsEnabled()) return false;

        BipedEntityRenderState state = ArmorHiderRenderContext.getState();
        if (!(state instanceof PlayerEntityRenderState playerState)) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return false;
        if (playerState.id != client.player.getId()) return false;

        EquipmentSlot slot = ArmorHiderRenderContext.getSlot();
        ItemStack stack = ArmorHiderRenderContext.getStack();
        if (slot == null || stack == null || stack.isEmpty()) return false;

        ItemStack equipped = switch (slot) {
            case HEAD -> state.equippedHeadStack;
            case CHEST -> state.equippedChestStack;
            case LEGS -> state.equippedLegsStack;
            case FEET -> state.equippedFeetStack;
            default -> ItemStack.EMPTY;
        };
        if (equipped == null || equipped.isEmpty()) return false;
        if (equipped != stack && !equipped.equals(stack)) return false;

        return switch (slot) {
            case HEAD -> ItemStackUtil.isSkull(stack) ? Runtime.isArmorHiderSkullEnabled() : Runtime.isArmorHiderHelmetEnabled();
            case CHEST -> ItemStackUtil.isElytra(stack) ? Runtime.isArmorHiderElytraEnabled() : Runtime.isArmorHiderChestplateEnabled();
            case LEGS -> Runtime.isArmorHiderLeggingsEnabled();
            case FEET -> Runtime.isArmorHiderBootsEnabled();
            default -> false;
        };
    }

    public static boolean shouldCancelHeadFeatureRender(PlayerEntityRenderState state) {
        if (!Runtime.isArmorHiderEnabled() || !Runtime.isArmorHiderSkullEnabled()) return false;
        if (state == null) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return false;
        if (state.id != client.player.getId()) return false;

        return state.wearingSkullType != null || ItemStackUtil.isSkull(state.equippedHeadStack);
    }

}
