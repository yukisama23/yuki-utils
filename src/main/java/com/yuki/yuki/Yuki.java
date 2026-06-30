package com.yuki.yuki;

import com.yuki.yuki.config.ConfigManager;
import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.hud.HudRegistry;
import com.yuki.yuki.util.LogSilencer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Yuki implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("yuki");

    @Override
    public void onInitializeClient() {
        LogSilencer.muteDandelionInfo();
        ConfigManager.safeLoad();
        Runtime.loadFromDisk(ConfigManager.CONFIG_PATH);
        HudRegistry.init();
        YukiTickHandler.init();
        YukiCommands.init();
    }

    public static void sendToggleChat(String moduleName, boolean enabled) {
        if (!Runtime.isChatNotificationsEnabled()) return;
        sendChat(Runtime.feedbackText(moduleName + " " + (enabled ? "enabled" : "disabled")));
    }

    public static void sendChat(Text text) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null && text != null) client.player.sendMessage(text, false);
    }

}
