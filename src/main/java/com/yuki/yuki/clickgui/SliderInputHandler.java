package com.yuki.yuki.clickgui;

import org.lwjgl.glfw.GLFW;

import java.util.IdentityHashMap;
import java.util.Map;

public final class SliderInputHandler {
    private final Map<GuiDefinition.SliderSetting, String> drafts = new IdentityHashMap<>();
    private GuiDefinition.SliderSetting focused;
    private GuiDefinition.SliderSetting dragging;

    public GuiDefinition.SliderSetting focused() {
        return focused;
    }

    public GuiDefinition.SliderSetting dragging() {
        return dragging;
    }

    public boolean isFocused(GuiDefinition.SliderSetting setting) {
        return focused == setting;
    }

    public String display(GuiDefinition.SliderSetting setting) {
        String draft = drafts.get(setting);
        return draft == null ? setting.text() : draft;
    }

    public String displayWithCursor(GuiDefinition.SliderSetting setting) {
        String value = display(setting);
        return focused == setting ? value + "_" : value;
    }

    public void focus(GuiDefinition.SliderSetting setting) {
        if (focused != null && focused != setting) commit();
        focused = setting;
        drafts.putIfAbsent(setting, setting.text());
    }

    public boolean keyPressed(int key) {
        if (focused == null) return false;
        if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
            commit();
            return true;
        }
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            discard();
            return true;
        }
        if (key == GLFW.GLFW_KEY_BACKSPACE) {
            String value = display(focused);
            if (!value.isEmpty()) setDraft(focused, value.substring(0, value.length() - 1));
            return true;
        }
        if (key == GLFW.GLFW_KEY_DELETE) {
            setDraft(focused, "");
            return true;
        }
        return false;
    }

    public boolean charTyped(String value) {
        if (focused == null) return false;
        append(focused, value);
        return true;
    }

    public void commit() {
        if (focused == null) return;
        GuiDefinition.SliderSetting setting = focused;
        String draft = drafts.remove(setting);
        if (draft != null) commitDraft(setting, draft);
        focused = null;
    }

    public void discard() {
        if (focused == null) return;
        drafts.remove(focused);
        focused = null;
    }

    public void clearFocus() {
        if (focused == null) return;
        drafts.remove(focused);
        focused = null;
    }

    public void beginDrag(GuiDefinition.SliderSetting setting, double mouseX, int x, int width) {
        preview(setting, mouseX, x, width);
        dragging = setting;
    }

    public void dragTo(double mouseX, int x, int width) {
        if (dragging != null) preview(dragging, mouseX, x, width);
    }

    public void releaseDrag() {
        if (dragging != null) dragging.saveCurrent();
        dragging = null;
    }

    public void cancelDrag() {
        dragging = null;
    }

    private void setDraft(GuiDefinition.SliderSetting setting, String value) {
        if (value == null) value = "";
        if (value.length() > 16) value = value.substring(0, 16);
        drafts.put(setting, value);
    }

    private void append(GuiDefinition.SliderSetting setting, String value) {
        if (value == null || value.isEmpty()) return;
        String current = display(setting);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if ((c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+') current += c;
        }
        setDraft(setting, current);
    }

    private void commitDraft(GuiDefinition.SliderSetting setting, String draft) {
        try {
            String value = draft.trim();
            if (value.isEmpty() || value.equals("-") || value.equals("+") || value.equals(".") || value.equals("-.") || value.equals("+.")) return;
            float parsed = Float.parseFloat(value);
            if (Float.isFinite(parsed)) setting.setTypedValue(parsed);
        } catch (NumberFormatException ignored) {
        }
    }

    private void preview(GuiDefinition.SliderSetting setting, double mouseX, int x, int width) {
        if (width <= 0) return;
        float progress = (float) ((mouseX - x) / width);
        progress = Math.max(0.0f, Math.min(1.0f, progress));
        setting.previewFloat(setting.min + (setting.max - setting.min) * progress);
    }
}
