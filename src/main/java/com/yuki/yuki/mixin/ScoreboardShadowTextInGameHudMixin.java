package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public class ScoreboardShadowTextInGameHudMixin {
    @Redirect(
        method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V"
        )
    )
    private void yuki$drawScoreboardText(DrawContext context, TextRenderer renderer, Text text, int x, int y, int color, boolean shadow) {
        boolean enabled = Runtime.isScoreboardEnabled() && Runtime.isScoreboardShadowedTextEnabled();
        context.drawText(renderer, text, x, y, color, shadow || enabled);
    }
}
