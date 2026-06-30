package com.yuki.yuki;

import com.yuki.yuki.clickgui.ClickGuiScreen;
import com.yuki.yuki.feature.player.AutoSprint;
import com.yuki.yuki.hud.HudEditorScreen;
import com.yuki.yuki.hud.HudRegistry;
import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.util.HitboxDistance;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public final class YukiTickHandler {
    private static boolean openClickGuiNextTick;
    private static boolean openHudNextTick;

    private static boolean wasAutoSprintEnabled;

    private YukiTickHandler() {}

    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(YukiTickHandler::onStartClientTick);
        ClientTickEvents.END_CLIENT_TICK.register(YukiTickHandler::onClientTick);
    }

    private static void onStartClientTick(MinecraftClient client) {
        if (client != null) HitboxDistance.updateCachedPlayer(client.player);
    }

    public static void openClickGuiNextTick() {
        openClickGuiNextTick = true;
    }

    public static void openHudEditorNextTick() {
        openHudNextTick = true;
    }

    private static void onClientTick(MinecraftClient client) {
        if (client != null) HitboxDistance.updateCachedPlayer(client.player);
        YukiKeyBinds.handleClientTickKeys(client);
        if (openClickGuiNextTick) {
            openClickGuiNextTick = false;
            client.setScreen(new ClickGuiScreen());
        }
        if (openHudNextTick) {
            openHudNextTick = false;
            client.setScreen(new HudEditorScreen());
        }

        boolean autoSprintEnabled = Runtime.isAutoSprintEnabled();
        if (autoSprintEnabled || wasAutoSprintEnabled) AutoSprint.onClientTick(client);
        wasAutoSprintEnabled = autoSprintEnabled;

        HudRegistry.tick(client);
    }
}
