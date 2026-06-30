package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class EffectsLivingEntityMixin {
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void yuki$effectsGlow(CallbackInfoReturnable<Boolean> cir) {
        if (Runtime.shouldDisableGlowEffect()) cir.setReturnValue(false);
    }
}
