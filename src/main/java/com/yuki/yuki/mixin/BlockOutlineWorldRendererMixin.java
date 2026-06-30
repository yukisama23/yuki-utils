package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.feature.visual.BlockOutline;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class BlockOutlineWorldRendererMixin {
    @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
    private void yuki$blockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, double x, double y, double z, OutlineRenderState state, int color, float lineWidth, CallbackInfo ci) {
        if (!Runtime.isBlockOutlineEnabled()) {
            BlockOutline.reset();
            return;
        }

        BlockOutline.render(matrices, vertexConsumer, x, y, z, state);
        ci.cancel();
    }
}
