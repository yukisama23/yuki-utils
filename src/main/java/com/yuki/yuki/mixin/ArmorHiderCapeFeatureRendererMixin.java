package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.util.ItemStackUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeFeatureRenderer.class)
public class ArmorHiderCapeFeatureRendererMixin {
    @Unique
    private ItemStack yuki$oldChestStack;

    @Unique
    private boolean yuki$patched;

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At("HEAD"))
    private void yuki$armorHiderCapeHead(net.minecraft.client.util.math.MatrixStack matrixStack, net.minecraft.client.render.command.OrderedRenderCommandQueue orderedRenderCommandQueue, int int2, PlayerEntityRenderState state, float float2, float float3, CallbackInfo ci) {
        if (!Runtime.isArmorHiderEnabled()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;
        if (state == null) return;
        if (state.id != client.player.getId()) return;
        ItemStack chestStack = state.equippedChestStack;
        if (chestStack == null || chestStack.isEmpty()) return;
        boolean elytra = ItemStackUtil.isElytra(chestStack);
        if (elytra ? !Runtime.isArmorHiderElytraEnabled() : !Runtime.isArmorHiderChestplateEnabled()) return;
        yuki$oldChestStack = chestStack;
        state.equippedChestStack = ItemStack.EMPTY;
        yuki$patched = true;
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/PlayerEntityRenderState;FF)V", at = @At("RETURN"))
    private void yuki$armorHiderCapeReturn(net.minecraft.client.util.math.MatrixStack matrixStack, net.minecraft.client.render.command.OrderedRenderCommandQueue orderedRenderCommandQueue, int int2, PlayerEntityRenderState state, float float2, float float3, CallbackInfo ci) {
        if (!yuki$patched) return;
        if (state != null) {
            state.equippedChestStack = yuki$oldChestStack == null ? ItemStack.EMPTY : yuki$oldChestStack;
        }
        yuki$oldChestStack = null;
        yuki$patched = false;
    }
}
