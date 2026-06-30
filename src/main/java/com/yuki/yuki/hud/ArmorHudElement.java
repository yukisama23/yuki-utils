package com.yuki.yuki.hud;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public final class ArmorHudElement implements HudElement {
    static final int SLOT_SIZE = 22;
    static final int STEP = 20;
    static final int ITEM_OFFSET = 3;
    private static final int ARMOR_SLOT_COUNT = 4;
    private static final int HOTBAR_SIDE_OFFSET = 98;
    private static final int HEIGHT = SLOT_SIZE;
    private static final Identifier HOTBAR_SPRITE = Identifier.of("minecraft", "hud/hotbar");

    private static final ItemStack EDITOR_HEAD = editorStack(new ItemStack(Items.NETHERITE_HELMET), 0.25f);
    private static final ItemStack EDITOR_CHEST = editorStack(new ItemStack(Items.ELYTRA), 0.45f);
    private static final ItemStack EDITOR_LEGS = editorStack(new ItemStack(Items.NETHERITE_LEGGINGS), 0.65f);
    private static final ItemStack EDITOR_FEET = editorStack(new ItemStack(Items.NETHERITE_BOOTS), 0.85f);

    @Override
    public String getName() {
        return "armor hud";
    }

    @Override
    public boolean isEnabled() {
        return Runtime.isArmorHudEnabled();
    }

    @Override
    public int getX(int screenWidth, int elementWidth, float scale) {
        if (Runtime.isArmorHudCustomPosition()) {
            return anchorX(screenWidth, elementWidth, scale, Runtime.getArmorHudX());
        }
        boolean right = "hotbar_right".equals(Runtime.getArmorHudPosition());
        int width = HudGeometry.scaled(elementWidth, scale);
        int offhandSpace = shouldReserveOffhandSpace(right) ? ArmorHudOffhandRenderer.RESERVED_WIDTH : 0;
        if (right) {
            return clampX(screenWidth, screenWidth / 2 + HOTBAR_SIDE_OFFSET + offhandSpace, width);
        }
        return clampX(screenWidth, screenWidth / 2 - HOTBAR_SIDE_OFFSET - offhandSpace - width, width);
    }

    @Override
    public int getY(int screenHeight, int elementHeight, float scale) {
        if (Runtime.isArmorHudCustomPosition()) {
            return anchorY(screenHeight, elementHeight, scale, Runtime.getArmorHudY());
        }
        int height = HudGeometry.scaled(elementHeight, scale);
        return Math.max(0, screenHeight - height);
    }

    @Override
    public float getScale() {
        return Runtime.isArmorHudCustomPosition() ? Runtime.getArmorHudScale() : 1.0f;
    }

    @Override
    public void setX(int x) {
        Runtime.setArmorHudX(x);
    }

    @Override
    public void setY(int y) {
        Runtime.setArmorHudY(y);
    }

    @Override
    public void setScale(float scale) {
        Runtime.setArmorHudScale(scale);
    }

    @Override
    public void resetToDefault() {
        Runtime.setArmorHudX(-1);
        Runtime.setArmorHudY(-1);
        Runtime.setArmorHudScale(1.0f);
    }

    @Override
    public int getLastWidth() {
        return liveWidth();
    }

    @Override
    public int getLastHeight() {
        return HEIGHT;
    }

    @Override
    public int getEditorWidth() {
        return editorWidth();
    }

    @Override
    public int getEditorHeight() {
        return HEIGHT;
    }

    @Override
    public void render(DrawContext ctx, int x, int y, float scale, boolean editor) {
        ItemStack head;
        ItemStack chest;
        ItemStack legs;
        ItemStack feet;
        int width;

        if (editor) {
            head = EDITOR_HEAD;
            chest = EDITOR_CHEST;
            legs = EDITOR_LEGS;
            feet = EDITOR_FEET;
            width = editorWidth();
        } else {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.player == null) {
                return;
            }

            head = client.player.getEquippedStack(EquipmentSlot.HEAD);
            chest = client.player.getEquippedStack(EquipmentSlot.CHEST);
            legs = client.player.getEquippedStack(EquipmentSlot.LEGS);
            feet = client.player.getEquippedStack(EquipmentSlot.FEET);

            int visibleSlots = visibleCount(head, chest, legs, feet);
            if (visibleSlots <= 0) {
                return;
            }
            width = widthFor(visibleSlots);
        }

        var matrices = ctx.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(scale, scale);

        if (Runtime.isArmorHudHotbarStyle()) {
            drawHotbarBackground(ctx, width);
        }

        int index = 0;
        index = drawIfPresent(ctx, head, index);
        index = drawIfPresent(ctx, chest, index);
        index = drawIfPresent(ctx, legs, index);
        drawIfPresent(ctx, feet, index);

        matrices.popMatrix();
    }

    @Override
    public void tick(MinecraftClient client) {}

    private static int liveWidth() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return widthFor(ARMOR_SLOT_COUNT);
        }

        ItemStack head = client.player.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chest = client.player.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack legs = client.player.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack feet = client.player.getEquippedStack(EquipmentSlot.FEET);
        int visibleSlots = visibleCount(head, chest, legs, feet);
        return widthFor(Math.max(1, visibleSlots));
    }

    private static int editorWidth() {
        return widthFor(ARMOR_SLOT_COUNT);
    }

    private static int visibleCount(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet) {
        int count = 0;
        if (!isEmpty(head)) count++;
        if (!isEmpty(chest)) count++;
        if (!isEmpty(legs)) count++;
        if (!isEmpty(feet)) count++;
        return count;
    }

    private static int widthFor(int count) {
        return SLOT_SIZE + (Math.max(1, count) - 1) * STEP;
    }

    static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.isEmpty();
    }

    private static int drawIfPresent(DrawContext ctx, ItemStack stack, int index) {
        if (isEmpty(stack)) {
            return index;
        }
        drawSlot(ctx, stack, index);
        return index + 1;
    }

    static void drawSlot(DrawContext ctx, ItemStack stack, int index) {
        if (isEmpty(stack)) {
            return;
        }
        int x = ITEM_OFFSET + index * STEP;
        int y = ITEM_OFFSET;
        ctx.drawItem(stack, x, y);
        drawDurabilityBar(ctx, stack, x, y);
    }

    static void drawHotbarBackground(DrawContext ctx, int width) {
        int bodyWidth = Math.max(0, width - 3);
        if (bodyWidth > 0) {
            ctx.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, SLOT_SIZE, 0, 0, 0, 0, bodyWidth, SLOT_SIZE);
        }
        ctx.drawGuiTexture(RenderPipelines.GUI_TEXTURED, HOTBAR_SPRITE, 182, SLOT_SIZE, 179, 0, bodyWidth, 0, 3, SLOT_SIZE);
    }

    static void drawDurabilityBar(DrawContext ctx, ItemStack stack, int x, int y) {
        if (stack == null || stack.isEmpty() || !stack.isDamageable() || stack.getMaxDamage() <= 0 || stack.getDamage() <= 0) {
            return;
        }

        int maxDamage = stack.getMaxDamage();
        int remaining = Math.max(0, maxDamage - stack.getDamage());
        int barWidth = Math.max(0, Math.round(13.0f * remaining / maxDamage));
        float ratio = Math.max(0.0f, Math.min(1.0f, (float) remaining / (float) maxDamage));
        int color = 0xFF000000 | MathHelper.hsvToRgb(ratio / 3.0f, 1.0f, 1.0f);
        int barX = x + 2;
        int barY = y + 13;

        ctx.fill(barX, barY, barX + 13, barY + 2, 0xFF000000);
        if (barWidth > 0) {
            ctx.fill(barX, barY, barX + barWidth, barY + 1, color);
        }
    }

    private static boolean shouldReserveOffhandSpace(boolean rightSide) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || isEmpty(client.player.getOffHandStack())) {
            return false;
        }
        return !Runtime.isArmorHudCustomPosition() && Runtime.isArmorHudOffhandEnabled() && ArmorHudOffhandRenderer.isOffhandOnRight(client.player) == rightSide;
    }

    private static ItemStack editorStack(ItemStack stack, float damageRatio) {
        if (stack.isDamageable() && stack.getMaxDamage() > 0) {
            int damage = MathHelper.clamp(Math.round(stack.getMaxDamage() * damageRatio), 1, stack.getMaxDamage() - 1);
            stack.setDamage(damage);
        }
        return stack;
    }

    private static int clampX(int screenWidth, int x, int width) {
        return Math.max(0, Math.min(screenWidth - Math.max(1, width), x));
    }
}
