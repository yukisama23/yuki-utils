package com.yuki.yuki.hud;

import com.yuki.yuki.Yuki;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HudRegistry {
    private static final Identifier LAYER_ID = Identifier.of("yuki", "yuki_hud_layer");
    private static final List<HudElement> ELEMENTS = new ArrayList<>();
    private static final List<HudElement> ELEMENT_VIEW = Collections.unmodifiableList(ELEMENTS);
    private static boolean inited = false;

    private HudRegistry() {}

    public static void init() {
        if (inited) return;
        inited = true;
        HudElementRegistry.attachElementBefore(VanillaHudElements.SLEEP, LAYER_ID, HudRegistry::renderHud);
        ELEMENTS.add(new ArmorHudElement());
        ELEMENTS.add(new ArmorHudOffhandElement());
        ELEMENTS.add(new EffectHudElement());
        ELEMENTS.add(new TotemTweaksHudElement());
        ELEMENTS.add(new TotemAlertHudElement());
        ELEMENTS.add(new ReachDisplayHudElement());
    }

    public static List<HudElement> getElements() {
        return ELEMENT_VIEW;
    }

    public static void tick(MinecraftClient client) {
        for (HudElement el : ELEMENTS) {
            if (!el.isEnabled()) continue;
            try {
                el.tick(client);
            } catch (RuntimeException t) {
                Yuki.LOGGER.warn("Hud tick error", t);
            }
        }
    }

    private static void renderHud(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null || client.options.hudHidden) return;
        if (client.currentScreen instanceof HudEditorScreen) return;
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        for (HudElement el : ELEMENTS) {
            if (!el.isEnabled()) continue;
            float scale = HudGeometry.safeScale(el.getScale());
            int x = el.getX(sw, el.getLastWidth(), scale);
            int y = el.getY(sh, el.getLastHeight(), scale);
            el.render(ctx, x, y, scale, false);
        }
        ArmorHudOffhandRenderer.renderFixed(ctx, sw, sh);
    }
}
