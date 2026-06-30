package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ClientPlayNetworkHandler.class)
public class AnimationFixClientPlayNetworkHandlerMixin {
    @Inject(method = "onEntityTrackerUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;writeUpdatedEntries(Ljava/util/List;)V"))
    private void yuki$animationFix$onPreTrackerUpdate(EntityTrackerUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || !Runtime.isAnimationFixEnabled()) return;
        if (packet.id() != client.player.getId()) return;

        for (DataTracker.SerializedEntry<?> entry : new ArrayList<>(packet.trackedValues())) {
            if (entry != null && entry.handler().equals(TrackedDataHandlerRegistry.ENTITY_POSE)) {
                packet.trackedValues().remove(entry);
                break;
            }
        }
    }
}
