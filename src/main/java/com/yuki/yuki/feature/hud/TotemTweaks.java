package com.yuki.yuki.feature.hud;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.hud.TotemAlertHudElement;
import com.yuki.yuki.util.InventoryUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.sound.SoundEvents;

public final class TotemTweaks {
    private static final int TOTEM_STATUS = 35;

    private TotemTweaks() {}

    public static boolean handleEntityStatus(EntityStatusS2CPacket packet) {
        if (!Runtime.isTotemTweaksEnabled() || packet == null || packet.getStatus() != TOTEM_STATUS) return false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null || client.player == null) return false;
        if (packet.getEntity(client.world) != client.player) return false;
        if (Runtime.isTotemTweaksTotemAlertEnabled()) {
            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 1.0F, 2.0F);
            TotemAlertHudElement.show(countTotemsAfterPop(client));
        }
        return Runtime.isTotemTweaksDisableAnimationsEnabled();
    }

    public static int countTotems(MinecraftClient client) {
        return InventoryUtil.countItem(client, Items.TOTEM_OF_UNDYING);
    }

    private static int countTotemsAfterPop(MinecraftClient client) {
        return Math.max(0, countTotems(client) - 1);
    }

    public static int colorForTotemCount(int count) {
        if (count > 8) return 0xFF55FF55;
        if (count > 5) return 0xFFFFFF55;
        return 0xFFFF5555;
    }

}
