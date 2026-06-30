package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.feature.visual.CustomTooltip;
import com.yuki.yuki.feature.hud.ShulkerTooltips;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DrawContext.class)
public class CustomTooltipDrawContextMixin {

    @Inject(method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;IILnet/minecraft/client/gui/tooltip/TooltipPositioner;Lnet/minecraft/util/Identifier;Z)V", at = @At("HEAD"), cancellable = true)
    private void yuki$customTooltip$drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, Identifier texture, boolean focused, CallbackInfo ci) {
        boolean shulkerTooltip = ShulkerTooltips.hasActiveShulkerTooltip();
        if (!Runtime.isCustomTooltipEnabled() && !shulkerTooltip) return;
        ci.cancel();
        CustomTooltip.queue(textRenderer, ShulkerTooltips.appendPreview(components), x, y, positioner, shulkerTooltip);
    }

    @Inject(method = "drawDeferredElements", at = @At("TAIL"))
    private void yuki$customTooltip$drawQueuedTooltips(CallbackInfo ci) {
        CustomTooltip.renderQueued((DrawContext) (Object) this);
    }
}
