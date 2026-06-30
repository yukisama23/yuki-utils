package com.yuki.yuki;

import com.yuki.yuki.clickgui.GuiDefinition;
import com.yuki.yuki.config.Runtime;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;

public final class YukiKeyBinds {
    private static final HashSet<Integer> MODULE_KEY_BINDS_DOWN = new HashSet<>();
    private static final HashSet<Integer> MODULE_KEY_BINDS_CURRENT = new HashSet<>();
    private static boolean clickGuiKeyBindDown;

    private YukiKeyBinds() {}

    public static void handleClientTickKeys(MinecraftClient client) {
        if (!handleClickGuiKeyBind(client)) handleModuleKeyBinds(client);
    }

    private static boolean handleClickGuiKeyBind(MinecraftClient client) {
        if (client == null || client.getWindow() == null || client.currentScreen != null) {
            clickGuiKeyBindDown = false;
            return false;
        }
        int code = Runtime.getClickGuiKeyBind();
        if (code == Runtime.getKeyBindNone()) {
            clickGuiKeyBindDown = false;
            return false;
        }
        boolean pressed = isKeyBindPressed(client.getWindow().getHandle(), code);
        boolean opened = pressed && !clickGuiKeyBindDown;
        if (opened) YukiTickHandler.openClickGuiNextTick();
        clickGuiKeyBindDown = pressed;
        return opened;
    }

    private static void handleModuleKeyBinds(MinecraftClient client) {
        if (client == null || client.getWindow() == null || client.currentScreen != null) {
            MODULE_KEY_BINDS_DOWN.clear();
            return;
        }

        MODULE_KEY_BINDS_CURRENT.clear();
        long handle = client.getWindow().getHandle();
        for (GuiDefinition.Category category : GuiDefinition.categories()) {
            if ("HUD".equals(category.name)) continue;
            for (GuiDefinition.Module module : category.modules) {
                if (!module.hasToggle()) continue;
                int code = Runtime.getModuleKeyBind(module.name);
                if (code == Runtime.getKeyBindNone()) continue;
                if (!isKeyBindPressed(handle, code)) continue;
                MODULE_KEY_BINDS_CURRENT.add(code);
                if (!MODULE_KEY_BINDS_DOWN.contains(code)) {
                    boolean enabled = module.toggle();
                    Yuki.sendToggleChat(module.name, enabled);
                }
            }
        }
        MODULE_KEY_BINDS_DOWN.clear();
        MODULE_KEY_BINDS_DOWN.addAll(MODULE_KEY_BINDS_CURRENT);
    }

    private static boolean isKeyBindPressed(long window, int code) {
        if (Runtime.isMouseKeyBind(code)) {
            int button = Runtime.keyBindToMouseButton(code);
            return button >= 0 && GLFW.glfwGetMouseButton(window, button) == GLFW.GLFW_PRESS;
        }
        return GLFW.glfwGetKey(window, code) == GLFW.GLFW_PRESS;
    }
}
