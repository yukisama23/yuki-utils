package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class ArmorHudOffhandInGameHudMixin {
    @Redirect(
        method = "renderHotbar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;getOffHandStack()Lnet/minecraft/item/ItemStack;"
        )
    )
    private ItemStack yuki$armorHudHideOffhandStack(PlayerEntity player) {
        if (Runtime.shouldHideVanillaOffhandHud()) {
            return ItemStack.EMPTY;
        }
        return player.getOffHandStack();
    }
}
