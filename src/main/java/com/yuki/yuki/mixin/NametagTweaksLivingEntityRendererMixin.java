package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.feature.visual.NametagTweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class NametagTweaksLivingEntityRendererMixin {
    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;D)Z", at = @At("RETURN"), cancellable = true)
    private void yuki$nametagTweaks$hasLabel(LivingEntity entity, double squaredDistanceToCamera, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!Runtime.isNameTagTweaksEnabled()) return;
        if (!Runtime.isNameTagTweaksShowOwnEnabled()) return;
        if (NametagTweaks.isInventoryRender()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        if (entity != client.player) return;
        Perspective perspective = client.options.getPerspective();
        if (perspective.isFirstPerson()) return;
        cir.setReturnValue(true);
    }
}
