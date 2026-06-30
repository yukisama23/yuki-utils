package com.yuki.yuki.command;

import com.yuki.yuki.clickgui.GuiLayoutManager;
import com.yuki.yuki.config.ColorResetRegistry;
import com.yuki.yuki.config.ConfigManager;
import com.yuki.yuki.config.Runtime;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public final class YukiResetCommand {
    private YukiResetCommand() {}

    public static int resetColors(FabricClientCommandSource source) {
        ColorResetRegistry.resetAll();
        source.sendFeedback(Runtime.feedbackText("colors reset"));
        return 1;
    }

    public static int resetGui(FabricClientCommandSource source) {
        GuiLayoutManager.resetSavedLayouts();
        source.sendFeedback(Runtime.feedbackText("gui reset"));
        return 1;
    }

    public static int resetSettings(FabricClientCommandSource source) {
        ConfigManager.resetSettings();
        source.sendFeedback(Runtime.feedbackText("settings reset"));
        return 1;
    }

    public static int resetAll(FabricClientCommandSource source) {
        ConfigManager.resetAll();
        GuiLayoutManager.resetSavedLayouts();
        source.sendFeedback(Runtime.feedbackText("all reset"));
        return 1;
    }
}
