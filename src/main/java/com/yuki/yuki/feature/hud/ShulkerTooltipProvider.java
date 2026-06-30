package com.yuki.yuki.feature.hud;

import com.yuki.yuki.hud.ShulkerTooltipPreview;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;

public final class ShulkerTooltipProvider {
    private ShulkerTooltipProvider() {}

    public static boolean isShulkerBox(ItemStack stack) {
        return stack != null
                && !stack.isEmpty()
                && stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }

    public static ShulkerTooltipPreview createPreview(ItemStack stack) {
        if (!isShulkerBox(stack)) return null;
        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container == null) return null;

        DefaultedList<ItemStack> slots = DefaultedList.ofSize(ShulkerTooltipPreview.SLOT_COUNT, ItemStack.EMPTY);
        container.copyTo(slots);

        ArrayList<ItemStack> stacks = new ArrayList<>(ShulkerTooltipPreview.SLOT_COUNT);
        boolean hasItems = false;
        for (ItemStack slotStack : slots) {
            ItemStack copy = slotStack == null || slotStack.isEmpty() ? ItemStack.EMPTY : slotStack.copy();
            if (!copy.isEmpty()) hasItems = true;
            stacks.add(copy);
        }
        return hasItems ? new ShulkerTooltipPreview(stacks) : null;
    }
}
