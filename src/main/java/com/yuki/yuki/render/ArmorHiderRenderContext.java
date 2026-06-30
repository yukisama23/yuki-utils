package com.yuki.yuki.render;

import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
public final class ArmorHiderRenderContext {
    private static final ThreadLocal<EquipmentSlot> SLOT = new ThreadLocal<>();
    private static final ThreadLocal<ItemStack> STACK = new ThreadLocal<>();
    private static final ThreadLocal<BipedEntityRenderState> STATE = new ThreadLocal<>();
    private ArmorHiderRenderContext() {}

    public static void set(EquipmentSlot slot, ItemStack stack, BipedEntityRenderState state) {
        SLOT.set(slot);
        STACK.set(stack);
        STATE.set(state);
    }

    public static EquipmentSlot getSlot() {
        return SLOT.get();
    }

    public static ItemStack getStack() {
        return STACK.get();
    }

    public static BipedEntityRenderState getState() {
        return STATE.get();
    }

    public static void clear() {
        SLOT.remove();
        STACK.remove();
        STATE.remove();
    }
}
