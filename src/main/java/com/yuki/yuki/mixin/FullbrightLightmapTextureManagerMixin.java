package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapTextureManager.class)
public abstract class FullbrightLightmapTextureManagerMixin {

    private static final float GAMMA = 1600.0f;
    private static final float AMBIENT = 1.0f;

    @Redirect(
        method = "update",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/dimension/DimensionType;ambientLight()F")
    )
    private float yuki$fullbrightAmbient(DimensionType instance) {
        if (Runtime.isFullbrightEnabled()) return AMBIENT;
        return instance.ambientLight();
    }

    @Redirect(
        method = "update",
        at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1)
    )
    private float yuki$fullbrightGamma(Double instance) {
        if (Runtime.isFullbrightEnabled()) return GAMMA;
        return instance.floatValue();
    }
}
