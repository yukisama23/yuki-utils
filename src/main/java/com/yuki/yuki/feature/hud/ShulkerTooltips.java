package com.yuki.yuki.feature.hud;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.hud.CenteredTooltipComponent;
import com.yuki.yuki.hud.ShulkerTooltipComponent;
import com.yuki.yuki.hud.ShulkerTooltipPreview;
import com.yuki.yuki.hud.TooltipSpacerComponent;
import com.yuki.yuki.mixin.OrderedTextTooltipComponentAccessor;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class ShulkerTooltips {
    private static final Pattern MORE_LINE = Pattern.compile(".*\\b(and\\s+)?\\d+\\s+more\\b.*");

    private static ItemStack cachedStack = ItemStack.EMPTY;
    private static ShulkerTooltipPreview cachedPreview;
    private static boolean activeShulker;
    private static boolean activePreview;

    private ShulkerTooltips() {}

    public static void setHoveredStack(ItemStack stack) {
        activeShulker = false;
        activePreview = false;
        if (!Runtime.isShulkerTooltipsEnabled() || Runtime.isShulkerTooltipsShiftOnlyEnabled() && !isShiftDown()) {
            clearCache();
            return;
        }
        if (!ShulkerTooltipProvider.isShulkerBox(stack)) {
            return;
        }

        activeShulker = true;
        if (!cachedStack.isEmpty() && ItemStack.areEqual(cachedStack, stack)) {
            activePreview = cachedPreview != null && !cachedPreview.isEmpty();
            return;
        }

        cachedStack = stack.copy();
        cachedPreview = ShulkerTooltipProvider.createPreview(stack);
        activePreview = cachedPreview != null && !cachedPreview.isEmpty();
    }

    private static boolean isShiftDown() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) return false;
        long handle = client.getWindow().getHandle();
        return GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS || GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }

    public static void clearHoveredStack() {
        activeShulker = false;
        activePreview = false;
    }

    public static boolean hasActiveShulkerTooltip() {
        return Runtime.isShulkerTooltipsEnabled() && activeShulker;
    }

    public static boolean hasActivePreview() {
        return hasActiveShulkerTooltip() && activePreview && cachedPreview != null && !cachedPreview.isEmpty();
    }

    public static List<TooltipComponent> appendPreview(List<TooltipComponent> components) {
        if (!hasActiveShulkerTooltip() || components == null || components.isEmpty()) {
            return components;
        }

        boolean hasPreview = hasActivePreview();
        ArrayList<TooltipComponent> tail = new ArrayList<>(components.size());
        for (int i = 1; i < components.size(); i++) {
            TooltipComponent component = components.get(i);
            if (shouldKeep(component)) tail.add(component);
        }

        ArrayList<TooltipComponent> out = new ArrayList<>(components.size() + (hasPreview ? 2 : 0));
        TooltipComponent title = components.get(0);
        out.add(new CenteredTooltipComponent(title, hasPreview ? ShulkerTooltipPreview.WIDTH : 0, componentText(title)));
        if (hasPreview) {
            out.add(new ShulkerTooltipComponent(cachedPreview));
            if (!tail.isEmpty()) out.add(TooltipSpacerComponent.LINE);
        }
        out.addAll(tail);
        return out;
    }

    private static boolean shouldKeep(TooltipComponent component) {
        String line = normalize(componentText(component));
        if (line.isEmpty()) return true;
        if (isMoreLine(line)) return false;
        return !isContainedItemLine(line);
    }

    private static boolean isMoreLine(String line) {
        return MORE_LINE.matcher(line).matches() || line.contains("还有") && line.matches(".*\\d+.*");
    }

    private static boolean isContainedItemLine(String line) {
        if (!hasActivePreview()) return false;
        for (ItemStack stack : cachedPreview.stacks()) {
            if (stack == null || stack.isEmpty()) continue;
            String itemName = normalize(stack.getName().getString());
            if (itemName.isEmpty()) continue;
            if (line.equals(itemName) || line.startsWith(itemName + " ") || line.contains(" " + itemName + " ")) return true;
        }
        return false;
    }

    private static String componentText(TooltipComponent component) {
        if (component instanceof OrderedTextTooltipComponentAccessor accessor) return orderedTextToString(accessor.yuki$getText());
        return "";
    }

    private static String orderedTextToString(OrderedText text) {
        if (text == null) return "";
        StringBuilder out = new StringBuilder();
        text.accept((index, style, codePoint) -> {
            out.appendCodePoint(codePoint);
            return true;
        });
        return out.toString();
    }

    private static String normalize(String text) {
        if (text == null || text.isEmpty()) return "";
        StringBuilder out = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if ((ch == '&' || ch == '§') && i + 1 < text.length()) {
                i++;
                continue;
            }
            out.append(ch);
        }
        return out.toString().trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private static void clearCache() {
        cachedStack = ItemStack.EMPTY;
        cachedPreview = null;
        activeShulker = false;
        activePreview = false;
    }
}
