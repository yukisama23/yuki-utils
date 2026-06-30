package com.yuki.yuki.mixin;

import com.yuki.yuki.feature.visual.ItemAnimations;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class ItemAnimationsLivingEntityMixin {
    @Inject(method = "swingHand", at = @At("TAIL"))
    private void yuki$onSwing(Hand hand, CallbackInfo ci) {
        if (!((Object) this instanceof ClientPlayerEntity)) return;
        ItemAnimations.onSwing(hand);
    }

    @Inject(method = "getHandSwingDuration", at = @At("HEAD"), cancellable = true)
    private void yuki$overrideThirdPersonSwingDuration(CallbackInfoReturnable<Integer> cir) {
        if (!((Object) this instanceof ClientPlayerEntity)) return;
        if (!ItemAnimations.shouldOverrideThirdPersonSwing()) return;
        cir.setReturnValue(ItemAnimations.getThirdPersonSwingDuration());
    }

    @Inject(method = "tickHandSwing", at = @At("TAIL"))
    private void yuki$onUpdateSwing(CallbackInfo ci) {
        if (!((Object) this instanceof ClientPlayerEntity)) return;
        ItemAnimations.onUpdateSwingTime();
        if (!ItemAnimations.shouldOverrideThirdPersonSwing()) return;
        LivingEntity self = (LivingEntity) (Object) this;
        self.lastHandSwingProgress = ItemAnimations.getPreviousSwingAnimation();
        self.handSwingProgress = ItemAnimations.getCurrentSwingAnimation();
    }
}
