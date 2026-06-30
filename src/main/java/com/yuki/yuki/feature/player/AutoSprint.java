package com.yuki.yuki.feature.player;
import com.yuki.yuki.config.Runtime;
import net.minecraft.client.MinecraftClient;

public final class AutoSprint {
    private AutoSprint() {}

    private static boolean forcedPress = false;

    public static void onClientTick(MinecraftClient client) {
        if (client == null || client.player == null || client.options == null) {
            forcedPress = false;
            return;
        }

        if (client.currentScreen != null) {
            release(client);
            return;
        }

        if (!Runtime.isAutoSprintEnabled()) {
            release(client);
            return;
        }

        client.options.sprintKey.setPressed(true);
        forcedPress = true;
    }

    private static void release(MinecraftClient client) {
        if (forcedPress && client != null && client.options != null) {
            client.options.sprintKey.setPressed(false);
        }
        forcedPress = false;
    }
}
