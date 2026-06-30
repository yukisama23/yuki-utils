package com.yuki.yuki.mixin;

import com.yuki.yuki.render.ArmorHiderPipeline;
import com.yuki.yuki.render.ArmorHiderRenderContext;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EquipmentRenderer.class)
public class ArmorHiderEquipmentRendererMixin {
    private static void yuki$setupContext(Object state, ItemStack stack) {
        if (!(state instanceof BipedEntityRenderState bipedState)) return;
        if (stack == null || stack.isEmpty()) return;
        EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);
        if (equippable == null) return;

        EquipmentSlot slot = equippable.slot();
        if (slot != EquipmentSlot.HEAD && slot != EquipmentSlot.CHEST && slot != EquipmentSlot.LEGS && slot != EquipmentSlot.FEET) return;
        ArmorHiderRenderContext.set(slot, stack, bipedState);
    }

    private static void yuki$cancelIfHidden(CallbackInfo ci) {
        if (!ArmorHiderPipeline.shouldCancelRender()) return;
        ArmorHiderRenderContext.clear();
        ci.cancel();
    }

    @Inject(
        method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;II)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private <S> void yuki$beforeStandardArmorRender(EquipmentModel.LayerType layerType, RegistryKey<?> assetKey, Model<? super S> model, S state, ItemStack stack, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int outlineColor, CallbackInfo ci) {
        yuki$setupContext(state, stack);
        yuki$cancelIfHidden(ci);
    }

    @Inject(
        method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;II)V",
        at = @At("RETURN")
    )
    private <S> void yuki$afterStandardArmorRender(EquipmentModel.LayerType layerType, RegistryKey<?> assetKey, Model<? super S> model, S state, ItemStack stack, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int outlineColor, CallbackInfo ci) {
        ArmorHiderRenderContext.clear();
    }

    @Inject(
        method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private <S> void yuki$beforeTexturedArmorRender(EquipmentModel.LayerType layerType, RegistryKey<?> assetKey, Model<? super S> model, S state, ItemStack stack, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, @Nullable Identifier textureId, int outlineColor, int initialOrder, CallbackInfo ci) {
        yuki$setupContext(state, stack);
        yuki$cancelIfHidden(ci);
    }

    @Inject(
        method = "render(Lnet/minecraft/client/render/entity/equipment/EquipmentModel$LayerType;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/util/Identifier;II)V",
        at = @At("RETURN")
    )
    private <S> void yuki$afterTexturedArmorRender(EquipmentModel.LayerType layerType, RegistryKey<?> assetKey, Model<? super S> model, S state, ItemStack stack, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, @Nullable Identifier textureId, int outlineColor, int initialOrder, CallbackInfo ci) {
        ArmorHiderRenderContext.clear();
    }
}
