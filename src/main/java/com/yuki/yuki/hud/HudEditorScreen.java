package com.yuki.yuki.hud;

import com.yuki.yuki.config.ConfigManager;
import com.yuki.yuki.config.Runtime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HudEditorScreen extends Screen {
    private static final int GUIDE_COLOR = 0x80FFFFFF;
    private static final Identifier CROSSHAIR = Identifier.of("minecraft", "hud/crosshair");
    private HudElement dragging;
    private HudElement selected;
    private double offX;
    private double offY;
    private boolean guideVertical;
    private boolean guideHorizontal;

    public HudEditorScreen() {
        super(Text.literal("hud editor"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int sw = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int sh = MinecraftClient.getInstance().getWindow().getScaledHeight();
        drawCrosshair(context, sw, sh);
        if (guideVertical) context.fill(sw / 2, 0, sw / 2 + 1, sh, GUIDE_COLOR);
        if (guideHorizontal) context.fill(0, sh / 2, sw, sh / 2 + 1, GUIDE_COLOR);
        var elements = HudRegistry.getElements();
        for (HudElement el : elements) {
            if (!shouldShowInEditor(el)) continue;
            HudGeometry.Bounds bounds = HudGeometry.editorBounds(el, sw, sh);
            el.render(context, bounds.x(), bounds.y(), bounds.scale(), true);
        }
        context.drawText(textRenderer, "drag to move, scroll to scale, right click to reset", 6, 6, 0xFFFFFFFF, true);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        guideVertical = false;
        guideHorizontal = false;
        if (click.button() == 1) {
            double mouseX = click.x();
            double mouseY = click.y();
            int sw = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int sh = MinecraftClient.getInstance().getWindow().getScaledHeight();
            var elements = HudRegistry.getElements();
            for (int i = elements.size() - 1; i >= 0; i--) {
                HudElement el = elements.get(i);
                if (!shouldShowInEditor(el)) continue;
                HudGeometry.Bounds bounds = HudGeometry.editorBounds(el, sw, sh);
                if (contains(mouseX, mouseY, bounds)) {
                    dragging = null;
                    selected = null;
                    el.resetToDefault();
                    ConfigManager.saveHud();
                    return true;
                }
            }
        }
        if (click.button() == 0) {
            double mouseX = click.x();
            double mouseY = click.y();
            int sw = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int sh = MinecraftClient.getInstance().getWindow().getScaledHeight();
            selected = null;
            var elements = HudRegistry.getElements();
            for (int i = elements.size() - 1; i >= 0; i--) {
                HudElement el = elements.get(i);
                if (!shouldShowInEditor(el)) continue;
                HudGeometry.Bounds bounds = HudGeometry.editorBounds(el, sw, sh);
                if (contains(mouseX, mouseY, bounds)) {
                    dragging = el;
                    selected = el;
                    offX = mouseX - bounds.x();
                    offY = mouseY - bounds.y();
                    return true;
                }
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (dragging != null && click.button() == 0) {
            double mouseX = click.x();
            double mouseY = click.y();
            int sw = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int sh = MinecraftClient.getInstance().getWindow().getScaledHeight();
            HudGeometry.Bounds bounds = HudGeometry.editorBounds(dragging, sw, sh);
            int w = bounds.scaledWidth();
            int h = bounds.scaledHeight();
            int nx = (int) Math.round(mouseX - offX);
            int ny = (int) Math.round(mouseY - offY);
            HudSnapHelper.Result snap = HudSnapHelper.snap(dragging, nx, ny, w, h, sw, sh, this::shouldShowInEditor);
            guideVertical = snap.centerX();
            guideHorizontal = snap.centerY();
            dragging.setPositionFromTopLeft(snap.x(), snap.y(), w, h);
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (click.button() == 0 && dragging != null) {
            dragging = null;
            guideVertical = false;
            guideHorizontal = false;
            ConfigManager.saveHud();
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (selected != null) {
            int dx = 0;
            int dy = 0;
            if (input.isLeft()) dx = -1;
            else if (input.isRight()) dx = 1;
            else if (input.isUp()) dy = -1;
            else if (input.isDown()) dy = 1;
            if (dx != 0 || dy != 0) {
                int sw = MinecraftClient.getInstance().getWindow().getScaledWidth();
                int sh = MinecraftClient.getInstance().getWindow().getScaledHeight();
                HudGeometry.Bounds bounds = HudGeometry.editorBounds(selected, sw, sh);
                int w = bounds.scaledWidth();
                int h = bounds.scaledHeight();
                HudSnapHelper.Result snap = HudSnapHelper.snap(selected, bounds.x() + dx, bounds.y() + dy, w, h, sw, sh, this::shouldShowInEditor);
                guideVertical = snap.centerX();
                guideHorizontal = snap.centerY();
                selected.setPositionFromTopLeft(snap.x(), snap.y(), w, h);
                ConfigManager.saveHud();
                return true;
            }
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        HudElement target = dragging != null ? dragging : selected;
        if (target != null) {
            float s = HudGeometry.safeScale(target.getScale());
            if (verticalAmount > 0) s += 0.5f;
            else if (verticalAmount < 0) s -= 0.5f;
            if (s < 0.5f) s = 0.5f;
            if (s > 5.0f) s = 5.0f;
            target.setScale(s);
            ConfigManager.saveHud();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void drawCrosshair(DrawContext context, int sw, int sh) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, CROSSHAIR, sw / 2 - 7, sh / 2 - 7, 15, 15);
    }

    private boolean shouldShowInEditor(HudElement element) {
        if (!element.isEnabled()) return false;
        return !(element instanceof ArmorHudElement) || Runtime.isArmorHudCustomPosition();
    }

    private boolean contains(double mouseX, double mouseY, HudGeometry.Bounds bounds) {
        return mouseX >= bounds.x()
            && mouseX <= bounds.x() + bounds.scaledWidth()
            && mouseY >= bounds.y()
            && mouseY <= bounds.y() + bounds.scaledHeight();
    }

    @Override
    public void close() {
        ConfigManager.saveHud();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

}
