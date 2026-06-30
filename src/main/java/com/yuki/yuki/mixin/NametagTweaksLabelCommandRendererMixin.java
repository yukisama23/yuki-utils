package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.BatchingRenderCommandQueue;
import net.minecraft.client.render.command.LabelCommandRenderer;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LabelCommandRenderer.class)
public class NametagTweaksLabelCommandRendererMixin {
    @Unique
    private static boolean yuki$nametagShadowEnabled;

    @Unique
    private static boolean yuki$nametagBackgroundHidden;

    @Inject(
        method = "render(Lnet/minecraft/client/render/command/BatchingRenderCommandQueue;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/font/TextRenderer;)V",
        at = @At("HEAD")
    )
    private void yuki$cacheNametagOptions(BatchingRenderCommandQueue queue, VertexConsumerProvider.Immediate immediate, TextRenderer renderer, CallbackInfo ci) {
        yuki$nametagShadowEnabled = Runtime.isNameTagTweaksEnabled() && Runtime.isNameTagTweaksShadowedTextEnabled();
        yuki$nametagBackgroundHidden = Runtime.isNameTagTweaksEnabled() && Runtime.isNameTagTweaksDisableBackgroundEnabled();
    }

    @Redirect(
        method = "render(Lnet/minecraft/client/render/command/BatchingRenderCommandQueue;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/font/TextRenderer;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)V"
        )
    )
    private void yuki$drawNametagText(TextRenderer renderer, Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextRenderer.TextLayerType layerType, int backgroundColor, int light) {
        renderer.draw(text, x, y, color, yuki$textShadow(shadow), matrix, vertexConsumers, layerType, yuki$backgroundColor(backgroundColor), light);
    }

    @Unique
    private static boolean yuki$textShadow(boolean shadow) {
        return yuki$nametagShadowEnabled || shadow;
    }

    @Unique
    private static int yuki$backgroundColor(int color) {
        return yuki$nametagBackgroundHidden ? 0 : color;
    }
}
