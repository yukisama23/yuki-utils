package com.yuki.yuki.feature.visual;

public final class NametagTweaks {
    private static boolean inventoryRender;

    private NametagTweaks() {}

    public static boolean isInventoryRender() {
        return inventoryRender;
    }

    public static void setInventoryRender(boolean value) {
        inventoryRender = value;
    }
}
