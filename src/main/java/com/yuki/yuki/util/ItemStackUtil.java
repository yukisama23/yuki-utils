package com.yuki.yuki.util;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public final class ItemStackUtil {
    private ItemStackUtil() {}

    public static boolean isElytra(ItemStack stack) {
        return "elytra".equals(itemPath(stack));
    }

    public static boolean isSkull(ItemStack stack) {
        String path = itemPath(stack);
        return path.endsWith("_head") || path.endsWith("_skull");
    }

    private static String itemPath(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return "";
        return Registries.ITEM.getId(stack.getItem()).getPath();
    }
}
