package com.yuki.yuki;

import com.yuki.yuki.command.YukiResetCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public final class YukiCommands {
    private YukiCommands() {}

    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> register(dispatcher));
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
            ClientCommandManager.literal("yuki")
                .executes(ctx -> {
                    YukiTickHandler.openClickGuiNextTick();
                    return 1;
                })
                .then(ClientCommandManager.literal("gui")
                    .executes(ctx -> {
                        YukiTickHandler.openClickGuiNextTick();
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("hud-editor")
                    .executes(ctx -> {
                        YukiTickHandler.openHudEditorNextTick();
                        return 1;
                    })
                )
                .then(ClientCommandManager.literal("reset")
                    .then(ClientCommandManager.literal("colors")
                        .executes(ctx -> YukiResetCommand.resetColors(ctx.getSource()))
                    )
                    .then(ClientCommandManager.literal("gui")
                        .executes(ctx -> YukiResetCommand.resetGui(ctx.getSource()))
                    )
                    .then(ClientCommandManager.literal("settings")
                        .executes(ctx -> YukiResetCommand.resetSettings(ctx.getSource()))
                    )
                    .then(ClientCommandManager.literal("all")
                        .executes(ctx -> YukiResetCommand.resetAll(ctx.getSource()))
                    )
                )
        );
    }
}
