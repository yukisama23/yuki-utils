package com.yuki.yuki.mixin;

import com.yuki.yuki.feature.hud.ShulkerTooltips;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class ShulkerTooltipsHandledScreenMixin {
    @Shadow
    protected Slot focusedSlot;

    @Inject(method = "drawMouseoverTooltip", at = @At("HEAD"))
    private void yuki$shulkerTooltips$captureStack(DrawContext context, int x, int y, CallbackInfo ci) {
        if (focusedSlot != null && focusedSlot.hasStack()) {
            ShulkerTooltips.setHoveredStack(focusedSlot.getStack());
        } else {
            ShulkerTooltips.clearHoveredStack();
        }
    }

    @Inject(method = "drawMouseoverTooltip", at = @At("TAIL"))
    private void yuki$shulkerTooltips$clearStack(DrawContext context, int x, int y, CallbackInfo ci) {
        ShulkerTooltips.clearHoveredStack();
    }
}
