package com.yuki.yuki.feature.misc;

import com.yuki.yuki.Yuki;
import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.mixin.ChatHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;

import java.util.List;

public final class ChatTweaks {
    private ChatTweaks() {}

    public static boolean mouseClicked(Click click) {
        if (!Runtime.isChatTweaksEnabled() || !Runtime.isChatTweaksCopyEnabled()) return false;
        if (click == null || click.button() != 1) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.inGameHud == null || client.keyboard == null) return false;
        String line = getClickedLine(client, click.x(), click.y());
        if (line == null || line.isBlank()) return false;
        client.keyboard.setClipboard(line);
        Yuki.sendChat(Runtime.feedbackText("chat copied"));
        return true;
    }

    private static String getClickedLine(MinecraftClient client, double mouseX, double mouseY) {
        ChatHud chatHud = client.inGameHud.getChatHud();
        if (chatHud == null || !chatHud.isChatFocused()) return null;
        ChatHudAccessor accessor = (ChatHudAccessor) chatHud;
        double scale = accessor.yuki$getChatScale();
        if (scale <= 0.0D) return null;
        double chatX = Math.floor((mouseX - 2.0D) / scale);
        double chatY = Math.floor((client.getWindow().getScaledHeight() - mouseY - 40.0D) / scale);
        if (chatX < 0.0D || chatY < 0.0D || chatX > accessor.yuki$getWidth()) return null;
        int lineHeight = Math.max(1, accessor.yuki$getLineHeight());
        int line = (int)(chatY / lineHeight);
        if (line < 0 || line >= chatHud.getVisibleLineCount()) return null;
        List<ChatHudLine.Visible> visible = accessor.yuki$getVisibleMessages();
        int index = line + accessor.yuki$getScrolledLines();
        if (index < 0 || index >= visible.size()) return null;
        return getFullMessageText(visible, index);
    }

    private static String getFullMessageText(List<ChatHudLine.Visible> visible, int index) {
        int bottom = index;
        while (bottom > 0 && !isEndOfEntry(visible.get(bottom))) bottom--;

        int top = index;
        while (top < visible.size() - 1 && !isEndOfEntry(visible.get(top + 1))) top++;

        StringBuilder out = new StringBuilder();
        for (int i = top; i >= bottom; i--) {
            String part = toPlainText(visible.get(i).content()).trim();
            if (part.isEmpty()) continue;
            out.append(part);
        }
        return out.toString().trim();
    }

    private static boolean isEndOfEntry(ChatHudLine.Visible line) {
        return line == null || line.endOfEntry();
    }

    private static String toPlainText(OrderedText text) {
        StringBuilder out = new StringBuilder();
        if (text != null) text.accept((index, style, codePoint) -> {
            out.appendCodePoint(codePoint);
            return true;
        });
        return out.toString();
    }
}
