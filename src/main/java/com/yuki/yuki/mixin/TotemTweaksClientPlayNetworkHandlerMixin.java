package com.yuki.yuki.mixin;

import com.yuki.yuki.feature.hud.TotemTweaks;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class TotemTweaksClientPlayNetworkHandlerMixin {
    @Inject(method = "onEntityStatus", at = @At("HEAD"), cancellable = true)
    private void yuki$totemTweaks(EntityStatusS2CPacket packet, CallbackInfo ci) {
        if (TotemTweaks.handleEntityStatus(packet)) ci.cancel();
    }
}
