package com.yuki.yuki.mixin;

import com.yuki.yuki.hud.ReachDisplayHudElement;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ReachDisplayClientPlayerInteractionManagerMixin {
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void yuki$reachDisplay$beforeAttack(PlayerEntity player, Entity target, CallbackInfo ci) {
        ReachDisplayHudElement.recordAttack(player, target);
    }
}
