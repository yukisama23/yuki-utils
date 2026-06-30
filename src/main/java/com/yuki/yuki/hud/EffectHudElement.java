package com.yuki.yuki.hud;

import com.yuki.yuki.config.Runtime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class EffectHudElement implements HudElement {
    private static final int ICON_SIZE = 18;
    private static final int TEXT_X = 22;
    private static final int ROW_HEIGHT = 24;
    private static final int LINE_GAP = 1;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int GRAY = 0xFFCFCFCF;
    private static final List<StatusEffectInstance> EDITOR_EFFECTS = List.of(
            new StatusEffectInstance(StatusEffects.SPEED, 90 * 20, 1),
            new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 240 * 20, 0)
    );

    private final List<EffectEntry> cachedEffects = new ArrayList<>();
    private int lastW = 1;
    private int lastH = 1;
    private int editorW = 1;
    private int editorH = 1;

    @Override
    public String getName() {
        return "effect hud";
    }

    @Override
    public boolean isEnabled() {
        return Runtime.isEffectHudEnabled();
    }

    @Override
    public int getX(int screenWidth, int elementWidth, float scale) {
        return anchorX(screenWidth, elementWidth, scale, Runtime.getEffectHudX());
    }

    @Override
    public int getY(int screenHeight, int elementHeight, float scale) {
        return anchorY(screenHeight, elementHeight, scale, Runtime.getEffectHudY());
    }

    @Override
    public float getScale() {
        return Runtime.getEffectHudScale();
    }

    @Override
    public void setX(int x) {
        Runtime.setEffectHudX(x);
    }

    @Override
    public void setY(int y) {
        Runtime.setEffectHudY(y);
    }

    @Override
    public void setScale(float scale) {
        Runtime.setEffectHudScale(scale);
    }

    @Override
    public void resetToDefault() {
        Runtime.setEffectHudX(-1);
        Runtime.setEffectHudY(-1);
        Runtime.setEffectHudScale(1.0f);
    }

    @Override
    public int getLastWidth() {
        return lastW;
    }

    @Override
    public int getLastHeight() {
        return lastH;
    }

    @Override
    public int getEditorWidth() {
        refreshEditorDimensions(MinecraftClient.getInstance());
        return editorW;
    }

    @Override
    public int getEditorHeight() {
        refreshEditorDimensions(MinecraftClient.getInstance());
        return editorH;
    }

    @Override
    public void render(DrawContext ctx, int x, int y, float scale, boolean editor) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) return;

        TextRenderer tr = client.textRenderer;
        List<EffectEntry> effects = editor ? editorEntries() : cachedEffects;
        if (editor) updateEditorDimensions(tr, effects);

        if (effects.isEmpty() && !editor) {
            return;
        }

        var matrices = ctx.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x, y);
        matrices.scale(scale, scale);

        if (editor) {
            ctx.fill(-1, -1, editorW + 1, editorH + 1, 0x40FFFFFF);
        }

        int rowY = 0;
        for (EffectEntry effect : effects) {
            drawEffect(ctx, tr, effect, rowY);
            rowY += ROW_HEIGHT;
        }

        matrices.popMatrix();
    }

    @Override
    public void tick(MinecraftClient client) {
        refreshCache(client);
    }

    private void refreshCache(MinecraftClient client) {
        if (client == null || client.player == null) {
            setCachedEffects(List.of(), client == null ? null : client.textRenderer);
            return;
        }

        ArrayList<StatusEffectInstance> raw = new ArrayList<>();
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            if (effect != null && effect.shouldShowIcon()) raw.add(effect);
        }
        Collections.sort(raw);

        ArrayList<EffectEntry> next = new ArrayList<>(raw.size());
        for (StatusEffectInstance effect : raw) {
            next.add(entry(effect));
        }
        setCachedEffects(next, client.textRenderer);
    }

    private void setCachedEffects(List<EffectEntry> next, TextRenderer tr) {
        if (sameEntries(cachedEffects, next)) return;
        cachedEffects.clear();
        cachedEffects.addAll(next);
        if (tr != null) updateLastDimensions(tr, cachedEffects);
        else if (cachedEffects.isEmpty()) {
            lastW = 1;
            lastH = 1;
        }
    }

    private void refreshEditorDimensions(MinecraftClient client) {
        if (client == null || client.textRenderer == null) return;
        updateEditorDimensions(client.textRenderer, editorEntries());
    }

    private void updateEditorDimensions(TextRenderer tr, List<EffectEntry> effects) {
        int[] size = dimensions(tr, effects);
        editorW = size[0];
        editorH = size[1];
    }

    private void updateLastDimensions(TextRenderer tr, List<EffectEntry> effects) {
        int[] size = dimensions(tr, effects);
        lastW = size[0];
        lastH = size[1];
    }

    private static int[] dimensions(TextRenderer tr, List<EffectEntry> effects) {
        if (effects.isEmpty()) {
            return new int[]{1, 1};
        }

        int maxTextWidth = 1;
        for (EffectEntry effect : effects) {
            maxTextWidth = Math.max(maxTextWidth, tr.getWidth(effect.name));
            maxTextWidth = Math.max(maxTextWidth, tr.getWidth(effect.time));
        }
        return new int[]{Math.max(1, TEXT_X + maxTextWidth), Math.max(1, (effects.size() * ROW_HEIGHT) - 2)};
    }

    private static boolean sameEntries(List<EffectEntry> current, List<EffectEntry> next) {
        if (current.size() != next.size()) return false;
        for (int i = 0; i < current.size(); i++) {
            if (!current.get(i).sameAs(next.get(i))) return false;
        }
        return true;
    }

    private static List<EffectEntry> editorEntries() {
        ArrayList<EffectEntry> out = new ArrayList<>(EDITOR_EFFECTS.size());
        for (StatusEffectInstance effect : EDITOR_EFFECTS) {
            out.add(entry(effect));
        }
        return out;
    }

    private void drawEffect(DrawContext ctx, TextRenderer tr, EffectEntry effect, int y) {
        if (effect.sprite != null) {
            ctx.drawGuiTexture(RenderPipelines.GUI_TEXTURED, effect.sprite, 0, y, ICON_SIZE, ICON_SIZE);
        }
        ctx.drawTextWithShadow(tr, effect.name, TEXT_X, y, WHITE);
        ctx.drawTextWithShadow(tr, effect.time, TEXT_X, y + tr.fontHeight + LINE_GAP, GRAY);
    }

    private static EffectEntry entry(StatusEffectInstance effect) {
        return new EffectEntry(sprite(effect.getEffectType()), name(effect), time(effect));
    }

    private static Identifier sprite(RegistryEntry<StatusEffect> effect) {
        if (effect == null) {
            return null;
        }
        Identifier id = Registries.STATUS_EFFECT.getId(effect.value());
        if (id == null) {
            return null;
        }
        return Identifier.of(id.getNamespace(), "mob_effect/" + id.getPath());
    }

    private static String name(StatusEffectInstance effect) {
        String name = englishName(effect);
        int level = effect.getAmplifier() + 1;
        return level > 1 ? name + " " + roman(level) : name;
    }

    private static String englishName(StatusEffectInstance effect) {
        Identifier id = Registries.STATUS_EFFECT.getId(effect.getEffectType().value());
        if (id != null) {
            return titleCase(id.getPath());
        }
        return titleCase(effect.getTranslationKey());
    }

    private static String titleCase(String raw) {
        if (raw == null || raw.isBlank()) {
            return "Unknown";
        }

        String value = raw;
        int dot = value.lastIndexOf('.');
        if (dot >= 0 && dot + 1 < value.length()) {
            value = value.substring(dot + 1);
        }
        value = value.replace('_', ' ').replace('-', ' ').trim();
        if (value.isEmpty()) {
            return "Unknown";
        }

        StringBuilder out = new StringBuilder(value.length());
        boolean capitalize = true;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!out.isEmpty() && out.charAt(out.length() - 1) != ' ') {
                    out.append(' ');
                }
                capitalize = true;
                continue;
            }
            out.append(capitalize ? Character.toUpperCase(c) : Character.toLowerCase(c));
            capitalize = false;
        }
        return out.toString();
    }

    private static String time(StatusEffectInstance effect) {
        if (effect.isInfinite()) {
            return "∞";
        }

        int totalSeconds = Math.max(0, (effect.getDuration() + 19) / 20);
        if (totalSeconds > 1000 * 60) {
            return "∞";
        }

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format(Locale.ROOT, "%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format(Locale.ROOT, "%d:%02d", minutes, seconds);
    }

    private static String roman(int value) {
        return switch (value) {
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(value);
        };
    }

    private static final class EffectEntry {
        private final Identifier sprite;
        private final String name;
        private final String time;

        private EffectEntry(Identifier sprite, String name, String time) {
            this.sprite = sprite;
            this.name = name;
            this.time = time;
        }

        private boolean sameAs(EffectEntry other) {
            return other != null
                    && Objects.equals(sprite, other.sprite)
                    && name.equals(other.name)
                    && time.equals(other.time);
        }
    }
}
