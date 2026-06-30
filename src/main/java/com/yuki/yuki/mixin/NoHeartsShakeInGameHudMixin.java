package com.yuki.yuki.mixin;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class NoHeartsShakeInGameHudMixin {

    @Redirect(
        method = "renderHealthBar",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I")
    )
    private int yuki$disableHeartsShake_lowHealthShake(Random random, int bound) {
        if (Runtime.isNoHeartsShakeEnabled()) return 0;
        return random.nextInt(bound);
    }

    @ModifyArg(
        method = "renderStatusBars",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V"
        ),
        index = 5
    )
    private int yuki$disableHeartsShake_disableRegenBounce(int regeneratingHeartIndex) {
        return Runtime.isNoHeartsShakeEnabled() ? -1 : regeneratingHeartIndex;
    }
}
