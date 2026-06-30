package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class HideDefenseIconInGameHudMixin {
    @Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
    private static void yuki$hideDefenseIcon_renderArmor(DrawContext context, PlayerEntity player, int x, int y, int a, int b, CallbackInfo ci) {
        if (Runtime.isHideDefenseIconEnabled()) {
            ci.cancel();
        }
    }
}
