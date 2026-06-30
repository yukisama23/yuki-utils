package com.yuki.yuki.hud;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ShulkerTooltipPreview {
    public static final int COLUMNS = 9;
    public static final int ROWS = 3;
    public static final int SLOT_COUNT = COLUMNS * ROWS;
    public static final int SLOT_SIZE = 18;
    public static final int PADDING_TOP = 2;
    public static final int WIDTH = COLUMNS * SLOT_SIZE;
    public static final int HEIGHT = PADDING_TOP + ROWS * SLOT_SIZE;

    private final List<ItemStack> stacks;

    public ShulkerTooltipPreview(List<ItemStack> stacks) {
        this.stacks = copyStacks(stacks);
    }

    public List<ItemStack> stacks() {
        return stacks;
    }

    public boolean isEmpty() {
        for (ItemStack stack : stacks) {
            if (stack != null && !stack.isEmpty()) return false;
        }
        return true;
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {
        if (stacks == null || stacks.isEmpty()) return List.of();
        ArrayList<ItemStack> out = new ArrayList<>(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack stack = i < stacks.size() ? stacks.get(i) : ItemStack.EMPTY;
            out.add(stack == null || stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
        }
        return Collections.unmodifiableList(out);
    }
}
