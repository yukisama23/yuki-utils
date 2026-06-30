package com.yuki.yuki.mixin;

import com.yuki.yuki.feature.misc.ChatTweaks;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenChatTweaksMixin {
    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void yuki$chatTweaks$mouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        if (ChatTweaks.mouseClicked(click)) cir.setReturnValue(true);
    }
}
