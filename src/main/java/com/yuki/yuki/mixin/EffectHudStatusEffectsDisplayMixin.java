package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectsDisplay.class)
public abstract class EffectHudStatusEffectsDisplayMixin {
    @Inject(method = "shouldHideStatusEffectHud", at = @At("HEAD"), cancellable = true)
    private void yuki$hideInventoryEffectLayout(CallbackInfoReturnable<Boolean> cir) {
        if (Runtime.shouldHideVanillaInventoryEffects()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void yuki$hideInventoryEffects(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        if (Runtime.shouldHideVanillaInventoryEffects()) {
            ci.cancel();
        }
    }
}
