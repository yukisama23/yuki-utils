package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.feature.visual.ItemAnimations;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class ItemAnimationsHeldItemRendererMixin {
    @Unique
    private static ItemStack yuki$currentStack = ItemStack.EMPTY;

    @Unique
    private static boolean yuki$isMapStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.isOf(Items.MAP) || stack.isOf(Items.FILLED_MAP);
    }

    @Unique
    private static boolean yuki$shouldApplyScale() {
        return ItemAnimations.isEnabled() && yuki$currentStack != null && !yuki$currentStack.isEmpty() && !yuki$isMapStack(yuki$currentStack);
    }

    @Unique
    private static boolean yuki$shouldOverrideSwingProgress(Hand hand, ItemStack stack) {
        if (!ItemAnimations.isEnabled() || !ItemAnimations.shouldAnimateHand(hand)) return false;
        if (stack != null && !stack.isEmpty()) return !yuki$isMapStack(stack);
        return Runtime.isItemAnimationsIgnoreEffectsEnabled();
    }

    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void yuki$captureCurrentStack(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, OrderedRenderCommandQueue commandQueue, int light, CallbackInfo ci) {
        yuki$currentStack = stack;
    }

    @ModifyVariable(method = "renderFirstPersonItem", at = @At("HEAD"), argsOnly = true, index = 5)
    private float yuki$modifySwingProgress(float swingProgress, AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float originalSwingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, OrderedRenderCommandQueue commandQueue, int light) {
        if (!yuki$shouldOverrideSwingProgress(hand, stack)) return swingProgress;
        return ItemAnimations.getSwingAnimation(tickDelta);
    }

    @Inject(method = "renderArmHoldingItem", at = @At("HEAD"))
    private void yuki$applyScaleToArm(MatrixStack matrices, OrderedRenderCommandQueue commandQueue, int light, float equipProgress, float swingProgress, Arm arm, CallbackInfo ci) {
        if (!yuki$shouldApplyScale()) return;
        ItemAnimations.applyScale(matrices);
    }

    @Inject(method = "renderItem", at = @At("HEAD"))
    private void yuki$applyScaleToItem(LivingEntity entity, ItemStack stack, ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue commandQueue, int light, CallbackInfo ci) {
        if (!ItemAnimations.isEnabled()) return;
        if (stack == null || stack.isEmpty()) return;
        if (yuki$isMapStack(stack)) return;
        ItemAnimations.applyScale(matrices);
    }
}
