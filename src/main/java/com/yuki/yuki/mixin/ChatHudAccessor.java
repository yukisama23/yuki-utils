package com.yuki.yuki.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(ChatHud.class)
public interface ChatHudAccessor {
    @Accessor("visibleMessages")
    List<ChatHudLine.Visible> yuki$getVisibleMessages();

    @Accessor("scrolledLines")
    int yuki$getScrolledLines();

    @Invoker("getWidth")
    int yuki$getWidth();

    @Invoker("getChatScale")
    double yuki$getChatScale();

    @Invoker("getLineHeight")
    int yuki$getLineHeight();
}
