package com.yuki.yuki.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class InventoryUtil {
    private static final int INVENTORY_SIZE = 36;

    private InventoryUtil() {}

    public static int countItem(MinecraftClient client, Item item) {
        if (client == null || client.player == null || item == null) return 0;

        int count = 0;
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            ItemStack stack = client.player.getInventory().getStack(slot);
            if (isItem(stack, item)) count += stack.getCount();
        }

        ItemStack offhand = client.player.getOffHandStack();
        if (isItem(offhand, item)) count += offhand.getCount();

        return count;
    }

    public static boolean isItem(ItemStack stack, Item item) {
        return stack != null && !stack.isEmpty() && item != null && stack.isOf(item);
    }
}
