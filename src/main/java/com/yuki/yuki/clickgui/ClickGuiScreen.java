package com.yuki.yuki.clickgui;

import com.yuki.yuki.Yuki;
import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.hud.HudEditorScreen;
import com.yuki.yuki.util.UiMath;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.yuki.yuki.clickgui.ClickGuiStyle.*;

public final class ClickGuiScreen extends Screen {
    private final GuiLayoutManager layout;
    private final List<CategoryView> categoryViews = new ArrayList<>();
    private final ColorDrafts colors = new ColorDrafts();
    private final Map<GuiDefinition.Module, GuiAnimation> moduleAnimations = new IdentityHashMap<>();
    private final GuiTextCache textCache = new GuiTextCache();
    private final SliderInputHandler sliders = new SliderInputHandler();

    private static final Set<String> EXPANDED_MODULE_NAMES = new HashSet<>();
    private final CategoryDragController categoryDrag = new CategoryDragController(CATEGORY_DRAG_THRESHOLD);
    private long lastAnimationNanos = System.nanoTime();
    private GuiDefinition.ColorSetting expandedColor;
    private GuiDefinition.KeySetting listeningKeyBind;
    private GuiDefinition.ColorSetting draggingColorArea;
    private GuiDefinition.ColorSetting draggingHue;
    private final Screen parent;

    public ClickGuiScreen() {
        this(null);
    }

    public ClickGuiScreen(Screen parent) {
        super(Text.literal("yuki clickgui"));
        this.parent = parent;
        this.layout = new GuiLayoutManager(CATEGORY_WIDTH, PANEL_GAP, MIN_VISIBLE_DRAG_HANDLE, CATEGORY_SNAP_DISTANCE);
        List<GuiDefinition.Category> categories = GuiDefinition.categories();
        int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        int x = layout.defaultStartX(screenWidth, categories.size());
        for (GuiDefinition.Category category : categories) {
            var saved = layout.savedLayout(category.name);
            int viewX = saved == null ? x : layout.clampX(screenWidth, saved.x());
            int viewY = saved == null ? 6 : layout.clampY(screenHeight, saved.y());
            long z = saved == null ? 0L : saved.z();
            CategoryView view = new CategoryView(category, viewX, viewY, saved != null && saved.open(), layout.interactionFor(z));
            categoryViews.add(view);
            x += CATEGORY_WIDTH + PANEL_GAP;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        textCache.use(textRenderer);
        updateAnimations();
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        context.fill(0, 0, width, height, 0x66000000);
        List<CategoryView> renderOrder = renderOrder();
        for (CategoryView view : renderOrder) {
            renderCategory(context, textRenderer, view, height, mouseX, mouseY);
        }
        renderHudButton(context, textRenderer, width, height, mouseX, mouseY);
    }

    private void renderCategory(DrawContext context, TextRenderer tr, CategoryView view, int screenHeight, int mouseX, int mouseY) {
        int width = CATEGORY_WIDTH;
        int bodyHeight = animatedBodyHeight(view, screenHeight);
        int panelHeight = categoryPanelHeight(bodyHeight);
        Ui.drawPanel(context, view.x - 2, view.y, width + 4, panelHeight, true);
        Text title = textCache.legacyLower("&l" + view.category.displayName);
        int titleX = view.x + Math.max(4, (width - tr.getWidth(title)) / 2);
        int titleY = view.y + TITLE_Y_OFFSET;
        context.drawText(tr, title, titleX, titleY, Ui.TEXT_CATEGORY, true);
        if (bodyHeight <= 0) return;

        List<GuiDefinition.Module> modules = visibleModules(view.category);
        int contentHeight = categoryContentHeight(modules);
        int fullBodyHeight = categoryBodyHeight(screenHeight, contentHeight);
        int bodyY = view.y + HEADER_HEIGHT + EXPANDED_BODY_TOP_PADDING;
        int maxScroll = Math.max(0, contentHeight - fullBodyHeight);
        view.scroll = UiMath.clamp(view.scroll, 0, maxScroll);
        GuiClip bodyClip = GuiClip.bounds(view.x - 2, bodyY, view.x + width + 2, bodyY + bodyHeight);
        bodyClip.enable(context);
        int rowY = bodyY - view.scroll;
        for (GuiDefinition.Module module : modules) {
            if (bodyClip.intersectsY(rowY, MODULE_HEIGHT)) renderModule(context, tr, module, view.x, rowY, width);
            rowY += MODULE_HEIGHT;
            int settingsHeight = animatedSettingsHeight(module);
            if (settingsHeight > 0) renderSettings(context, tr, module, view.x, rowY, width, bodyClip, settingsHeight, mouseX, mouseY);
            rowY += settingsHeight;
        }
        context.disableScissor();
    }

    private void renderModule(DrawContext context, TextRenderer tr, GuiDefinition.Module module, int x, int y, int width) {
        String label = module.displayName;
        int textX = x + Math.max(4, (width - ClickGuiText.strippedWidth(textCache, label)) / 2);
        if (module.active()) {
            ClickGuiText.gradientShadow(context, tr, label, textX, y + 4, Ui.blue(), Ui.blue2());
        } else {
            context.drawText(tr, textCache.legacyLower(label), textX, y + 4, Ui.TEXT_DISABLED, true);
        }
    }

    private void renderSettings(DrawContext context, TextRenderer tr, GuiDefinition.Module module, int x, int y, int width, GuiClip bodyClip, int visibleHeight, int mouseX, int mouseY) {
        GuiClip settingsClip = bodyClip.intersect(GuiClip.bounds(x - 2, y, x + width + 2, y + visibleHeight));
        if (settingsClip.isEmpty()) return;
        settingsClip.enable(context);
        int settingY = y;
        int visibleBottom = y + visibleHeight;
        for (GuiDefinition.Setting setting : module.settings) {
            if (!setting.visible()) continue;
            int height = settingHeight(setting);
            if (isRenderableSetting(setting, settingY, height, visibleBottom) && settingsClip.intersectsY(settingY, height)) {
                renderSetting(context, tr, setting, x, settingY, width, mouseX, mouseY);
            }
            settingY += height;
        }
        context.disableScissor();
    }

    private void renderSetting(DrawContext context, TextRenderer tr, GuiDefinition.Setting setting, int x, int y, int width, int mouseX, int mouseY) {
        if (setting instanceof GuiDefinition.ColorSetting s) {
            ClickGuiText.shadow(context, tr, textCache, Ui.subSettingText(colors.displayHex(s)), x + SETTING_LABEL_X_OFFSET, y + SETTING_LABEL_Y_OFFSET, Ui.TEXT_SETTING);
            renderColor(context, tr, s, x, y, width, mouseX, mouseY);
        } else if (setting instanceof GuiDefinition.KeySetting s) {
            drawKeybindSetting(context, tr, s, x, y, width);
        } else if (setting instanceof GuiDefinition.BoolSetting s) {
            ClickGuiText.shadow(context, tr, textCache, Ui.subSettingText(setting.name), x + SETTING_LABEL_X_OFFSET, y + SETTING_LABEL_Y_OFFSET, s.get.get() ? Ui.TEXT_SETTING_VALUE : Ui.TEXT_SETTING);
        } else if (setting instanceof GuiDefinition.SliderSetting s) {
            ClickGuiText.shadow(context, tr, textCache, Ui.subSettingText(setting.name), x + SETTING_LABEL_X_OFFSET, y + SETTING_LABEL_Y_OFFSET, Ui.TEXT_SETTING);
            String value = sliders.displayWithCursor(s);
            ClickGuiText.value(context, tr, textCache, value, valueTextX(x, width, value), y + SETTING_LABEL_Y_OFFSET, Ui.TEXT_SETTING_VALUE);
            int sx = sliderTrackX(x);
            int sw = sliderTrackWidth(width);
            int sy = y + SLIDER_Y_OFFSET;
            Ui.drawSliderTrack(context, sx, sy, sw, SLIDER_TRACK_HEIGHT);
            float p = (s.getFloat() - s.min) / (s.max - s.min);
            p = Math.max(0.0f, Math.min(1.0f, p));
            int progressW = Math.round(sw * p);
            if (progressW > 0) Ui.drawSliderProgressOutline(context, sx, sy, progressW, SLIDER_TRACK_HEIGHT);
            int knob = sx + Math.round((sw - SLIDER_KNOB_WIDTH) * p);
            Ui.drawSliderKnob(context, knob, sy - 4, SLIDER_KNOB_WIDTH, SLIDER_KNOB_HEIGHT, true);
        } else if (setting instanceof GuiDefinition.CycleSetting s) {
            String value = displayCycleValue(s);
            if (!setting.name.isEmpty()) {
                ClickGuiText.shadow(context, tr, textCache, Ui.subSettingText(setting.name), x + SETTING_LABEL_X_OFFSET, y + SETTING_LABEL_Y_OFFSET, Ui.TEXT_SETTING);
                ClickGuiText.value(context, tr, textCache, value, valueTextX(x, width, value), y + SETTING_LABEL_Y_OFFSET, Ui.TEXT_SETTING_VALUE);
            } else {
                ClickGuiText.value(context, tr, textCache, value, x + CONTROL_X_PADDING, y + SETTING_LABEL_Y_OFFSET, Ui.TEXT_SETTING_VALUE);
            }
        }
    }

    private void renderColor(DrawContext context, TextRenderer tr, GuiDefinition.ColorSetting setting, int x, int y, int width, int mouseX, int mouseY) {
        int swatchX = x + width - COLOR_SWATCH_SIZE - 12;
        int swatchY = y + 5;
        Ui.drawColorSwatch(context, swatchX, swatchY, COLOR_SWATCH_SIZE, colors.display(setting), UiMath.contains(mouseX, mouseY, swatchX - 2, swatchY - 2, COLOR_SWATCH_SIZE + 4, COLOR_SWATCH_SIZE + 4));
        if (expandedColor == setting) renderColorPicker(context, tr, setting, x, y + COLOR_PICKER_Y_OFFSET, width, mouseX, mouseY);
    }

    private void renderColorPicker(DrawContext context, TextRenderer tr, GuiDefinition.ColorSetting setting, int x, int y, int width, int mouseX, int mouseY) {
        ColorPickerComponent.render(context, x, y, width, colors.displayHsv(setting));
    }

    private static int colorPickerSize(int width) {
        return ColorPickerComponent.size(width);
    }

    private void drawKeybindSetting(DrawContext context, TextRenderer tr, GuiDefinition.KeySetting setting, int x, int y, int width) {
        int textX = x + SETTING_LABEL_X_OFFSET;
        int textY = y + SETTING_LABEL_Y_OFFSET;
        ClickGuiText.shadow(context, tr, textCache, "keybind", textX, textY, Ui.TEXT_SETTING);
        String value = keyValueLabel(setting);
        int valueX = Math.max(textX + textCache.width("keybind") + 6, x + width - SETTING_LABEL_X_OFFSET - textCache.width(value));
        int valueColor = "none".equalsIgnoreCase(value) ? Ui.TEXT_SETTING : Ui.TEXT_SETTING_VALUE;
        ClickGuiText.value(context, tr, textCache, value, valueX, textY, valueColor);
    }

    private String keyValueLabel(GuiDefinition.KeySetting setting) {
        if (listeningKeyBind == setting) return "press...";
        String value = setting.text();
        return "none".equalsIgnoreCase(value) ? "none" : value;
    }

    private void renderHudButton(DrawContext context, TextRenderer tr, int width, int height, int mouseX, int mouseY) {
        int bx = width - HUD_BUTTON_WIDTH - HUD_BUTTON_MARGIN;
        int by = height - HUD_BUTTON_HEIGHT - HUD_BUTTON_MARGIN;
        ClickGuiText.hudButton(context, tr, textCache, bx, by, HUD_BUTTON_WIDTH, HUD_BUTTON_HEIGHT, "Open HUD Editor");
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        textCache.use(textRenderer);
        double x = click.x();
        double y = click.y();
        if (listeningKeyBind != null && click.button() != GLFW.GLFW_MOUSE_BUTTON_1) {
            listeningKeyBind.set(Runtime.mouseButtonToKeyBind(click.button()));
            listeningKeyBind = null;
            return true;
        }
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        if (UiMath.contains(x, y, width - HUD_BUTTON_WIDTH - HUD_BUTTON_MARGIN, height - HUD_BUTTON_HEIGHT - HUD_BUTTON_MARGIN, HUD_BUTTON_WIDTH, HUD_BUTTON_HEIGHT) && click.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
            colors.commitAll();
            MinecraftClient.getInstance().setScreen(new HudEditorScreen());
            return true;
        }
        CategoryView topmost = topmostUnderCursor(renderOrder(), (int) x, (int) y);
        if (topmost != null) {
            bringToFront(topmost);
            if (containsHeader(topmost, x, y)) {
                if (click.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                    toggleCategory(topmost);
                    return true;
                }
                if (click.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
                    startDraggingCategory(topmost, x, y);
                    return true;
                }
            }
        }
        HitSetting hitSetting = hitSetting(x, y);
        if (sliders.focused() != null && (hitSetting == null || hitSetting.setting() != sliders.focused())) sliders.commit();
        if (hitSetting != null && click.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
            bringToFront(hitSetting.view());
            if (clickSetting(hitSetting.setting(), x, y, hitSetting.x(), hitSetting.y(), hitSetting.width())) return true;
        }
        HitModule hit = hitModule(x, y);
        if (hit != null) {
            bringToFront(hit.view());
            if (click.button() == GLFW.GLFW_MOUSE_BUTTON_1) {
                if (hit.module().hasToggle()) {
                    boolean enabled = hit.module().toggle();
                    Yuki.sendToggleChat(hit.module().name, enabled);
                    return true;
                }
            }
            if (click.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && !hit.module().settings.isEmpty()) {
                toggleExpanded(hit.module());
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (categoryDrag.active()) {
            int screenWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
            int screenHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
            CategoryView moved = categoryDrag.update(click.x(), click.y(), layout, categoryBounds(), screenWidth, screenHeight);
            if (moved != null) markCategoryLayoutDirty(moved);
            return true;
        }
        if (sliders.dragging() != null) {
            HitSetting hit = hitSetting(sliders.dragging());
            if (hit != null) sliders.dragTo(click.x(), sliderTrackX(hit.x()), sliderTrackWidth(hit.width()));
            return true;
        }
        if (draggingColorArea != null) {
            HitSetting hit = hitSetting(draggingColorArea);
            if (hit != null) colors.setFromPicker(draggingColorArea, click.x(), click.y(), hit.x(), hit.y() + COLOR_PICKER_Y_OFFSET, hit.width());
            return true;
        }
        if (draggingHue != null) {
            HitSetting hit = hitSetting(draggingHue);
            if (hit != null) colors.setFromHue(draggingHue, click.y(), hit.x(), hit.y() + COLOR_PICKER_Y_OFFSET, hit.width());
            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (categoryDrag.dragging()) persistCategoryLayoutsIfDirty();
        categoryDrag.clear();
        if (draggingColorArea != null) colors.commit(draggingColorArea);
        if (draggingHue != null) colors.commit(draggingHue);
        sliders.releaseDrag();
        draggingColorArea = null;
        draggingHue = null;
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        CategoryView topmost = topmostUnderCursor(renderOrder(), (int) mouseX, (int) mouseY);
        if (topmost != null && containsBody(topmost, mouseX, mouseY)) {
            int contentHeight = categoryContentHeight(visibleModules(topmost.category));
            int bodyHeight = categoryBodyHeight(MinecraftClient.getInstance().getWindow().getScaledHeight(), contentHeight);
            int maxScroll = Math.max(0, contentHeight - bodyHeight);
            topmost.scroll = UiMath.clamp(topmost.scroll - (int) Math.round(verticalAmount * 18), 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (listeningKeyBind != null) {
            int key = input.key();
            if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE) listeningKeyBind.set(Runtime.getKeyBindNone());
            else if (key != GLFW.GLFW_KEY_UNKNOWN) listeningKeyBind.set(key);
            listeningKeyBind = null;
            return true;
        }
        if (sliders.keyPressed(input.key())) return true;
        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (sliders.focused() != null) {
            if (input.isValidChar()) sliders.charTyped(input.asString());
            return true;
        }
        return super.charTyped(input);
    }

    @Override
    public void close() {
        colors.commitAll();
        sliders.releaseDrag();
        categoryDrag.clear();
        persistCategoryLayoutsIfDirty();
        sliders.commit();
        if (parent != null) {
            MinecraftClient.getInstance().setScreen(parent);
        } else {
            super.close();
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void toggleCategory(CategoryView view) {
        colors.commitAll();
        sliders.commit();
        expandedColor = null;
        listeningKeyBind = null;
        if (view.open) {
            sliders.cancelDrag();
            draggingColorArea = null;
            draggingHue = null;
        }
        view.open = !view.open;
        view.openAnimation.setOpen(view.open);
        markCategoryLayoutDirty(view);
        persistCategoryLayoutsIfDirty();
    }

    private void startDraggingCategory(CategoryView view, double mouseX, double mouseY) {
        sliders.commit();
        categoryDrag.begin(view, mouseX, mouseY);
    }

    private void markCategoryLayoutDirty(CategoryView view) {
        if (view == null) return;
        layout.markDirty(view.category.name, view.x, view.y, view.open, view.lastInteracted);
    }

    private void persistCategoryLayoutsIfDirty() {
        layout.saveIfDirty(categoryLayoutSnapshots());
    }

    private List<GuiLayoutManager.CategoryLayoutSnapshot> categoryLayoutSnapshots() {
        ArrayList<GuiLayoutManager.CategoryLayoutSnapshot> snapshots = new ArrayList<>();
        for (CategoryView view : categoryViews) {
            snapshots.add(new GuiLayoutManager.CategoryLayoutSnapshot(view.category.name, view.x, view.y, view.open, view.lastInteracted));
        }
        return snapshots;
    }

    private List<GuiLayoutManager.CategoryBounds> categoryBounds() {
        ArrayList<GuiLayoutManager.CategoryBounds> bounds = new ArrayList<>();
        for (CategoryView view : categoryViews) bounds.add(new GuiLayoutManager.CategoryBounds(view.category.name, view.x, view.y));
        return bounds;
    }

    private boolean clickSetting(GuiDefinition.Setting setting, double mx, double my, int x, int y, int width) {
        if (setting instanceof GuiDefinition.ColorSetting s) {
            return clickColor(s, mx, my, x, y, width);
        } else if (setting instanceof GuiDefinition.KeySetting s) {
            if (UiMath.contains(mx, my, x + 2, y, width - 4, SETTING_HEIGHT)) {
                listeningKeyBind = s;
                return true;
            }
        } else if (setting instanceof GuiDefinition.BoolSetting s) {
            if (UiMath.contains(mx, my, x + 2, y, width - 4, SETTING_HEIGHT)) {
                s.set.accept(!s.get.get());
                return true;
            }
        } else if (setting instanceof GuiDefinition.SliderSetting s) {
            String value = sliders.displayWithCursor(s);
            int valueX = valueTextX(x, width, value);
            int valueWidth = textCache.width(value);
            if (UiMath.contains(mx, my, valueX, y + SETTING_LABEL_Y_OFFSET, valueWidth, 8)) {
                sliders.focus(s);
                listeningKeyBind = null;
                return true;
            }
            if (sliders.isFocused(s)) sliders.commit();
            int sx = sliderTrackX(x);
            int sw = sliderTrackWidth(width);
            int sy = y + SLIDER_Y_OFFSET;
            if (UiMath.contains(mx, my, sx, sy - 6, sw, 18)) {
                sliders.beginDrag(s, mx, sx, sw);
                return true;
            }
        } else if (setting instanceof GuiDefinition.CycleSetting s) {
            String value = displayCycleValue(s);
            int valueX = setting.name.isEmpty() ? x + CONTROL_X_PADDING : valueTextX(x, width, value);
            int valueWidth = textCache.width(value);
            if (UiMath.contains(mx, my, valueX, y + SETTING_LABEL_Y_OFFSET, valueWidth, 8)) {
                s.next(1);
                return true;
            }
        }
        return false;
    }

    private boolean clickColor(GuiDefinition.ColorSetting setting, double mx, double my, int x, int y, int width) {
        int swatchX = x + width - COLOR_SWATCH_SIZE - 12;
        int swatchY = y + 5;
        if (UiMath.contains(mx, my, swatchX - 2, swatchY - 2, COLOR_SWATCH_SIZE + 4, COLOR_SWATCH_SIZE + 4)) {
            expandedColor = expandedColor == setting ? null : setting;
            return true;
        }
        if (expandedColor == setting) {
            ColorPickerComponent.Hit colorHit = ColorPickerComponent.hit(mx, my, x, y + COLOR_PICKER_Y_OFFSET, width);
            if (colorHit == ColorPickerComponent.Hit.PICKER) {
                colors.setFromPicker(setting, mx, my, x, y + COLOR_PICKER_Y_OFFSET, width);
                draggingColorArea = setting;
                return true;
            }
            if (colorHit == ColorPickerComponent.Hit.HUE) {
                colors.setFromHue(setting, my, x, y + COLOR_PICKER_Y_OFFSET, width);
                draggingHue = setting;
                return true;
            }
        }
        return false;
    }

    private void toggleExpanded(GuiDefinition.Module module) {
        colors.commitAll();
        GuiAnimation animation = moduleAnimation(module);
        if (EXPANDED_MODULE_NAMES.remove(module.name)) {
            expandedColor = null;
            listeningKeyBind = null;
            sliders.clearFocus();
            animation.setOpen(false);
            return;
        }
        EXPANDED_MODULE_NAMES.add(module.name);
        animation.setOpen(true);
    }

    private boolean isExpanded(GuiDefinition.Module module) {
        return EXPANDED_MODULE_NAMES.contains(module.name);
    }

    private List<CategoryView> renderOrder() {
        ArrayList<CategoryView> out = new ArrayList<>(categoryViews);
        out.sort(Comparator.comparingLong(v -> v.lastInteracted));
        return out;
    }

    private CategoryView topmostUnderCursor(List<CategoryView> renderOrder, int x, int y) {
        for (int i = renderOrder.size() - 1; i >= 0; i--) {
            CategoryView view = renderOrder.get(i);
            if (containsPanel(view, x, y)) return view;
        }
        return null;
    }

    private void bringToFront(CategoryView view) {
        if (view == null) return;
        long next = layout.nextInteraction();
        if (view.lastInteracted == next) return;
        view.lastInteracted = next;
        markCategoryLayoutDirty(view);
    }

    private HitModule hitModule(double mouseX, double mouseY) {
        List<CategoryView> order = renderOrder();
        for (int i = order.size() - 1; i >= 0; i--) {
            CategoryView view = order.get(i);
            if (!containsBody(view, mouseX, mouseY)) continue;
            List<GuiDefinition.Module> modules = visibleModules(view.category);
            int cy = view.y + HEADER_HEIGHT + EXPANDED_BODY_TOP_PADDING - view.scroll;
            for (GuiDefinition.Module module : modules) {
                if (mouseY >= cy && mouseY <= cy + MODULE_HEIGHT && mouseX >= view.x && mouseX <= view.x + CATEGORY_WIDTH) return new HitModule(view, module);
                cy += moduleHeight(module);
            }
        }
        return null;
    }

    private HitSetting hitSetting(double mouseX, double mouseY) {
        List<CategoryView> order = renderOrder();
        for (int i = order.size() - 1; i >= 0; i--) {
            CategoryView view = order.get(i);
            if (!containsBody(view, mouseX, mouseY)) continue;
            HitSetting hit = hitSetting(view, mouseX, mouseY);
            if (hit != null) return hit;
        }
        return null;
    }

    private HitSetting hitSetting(CategoryView view, double mouseX, double mouseY) {
        int width = CATEGORY_WIDTH;
        int cy = view.y + HEADER_HEIGHT + EXPANDED_BODY_TOP_PADDING - view.scroll;
        for (GuiDefinition.Module module : visibleModules(view.category)) {
            cy += MODULE_HEIGHT;
            int visibleSettingsHeight = animatedSettingsHeight(module);
            if (visibleSettingsHeight > 0) {
                int settingY = cy;
                int visibleBottom = cy + visibleSettingsHeight;
                for (GuiDefinition.Setting setting : module.settings) {
                    if (!setting.visible()) continue;
                    int height = settingHeight(setting);
                    if (isHitTestableSetting(setting, settingY, height, visibleBottom) && containsSetting(settingY, height, visibleBottom, mouseX, mouseY, view.x, width)) {
                        return new HitSetting(view, setting, view.x, settingY, width);
                    }
                    settingY += height;
                }
            }
            cy += visibleSettingsHeight;
        }
        return null;
    }

    private HitSetting hitSetting(GuiDefinition.Setting target) {
        for (CategoryView view : categoryViews) {
            int width = CATEGORY_WIDTH;
            int cy = view.y + HEADER_HEIGHT + EXPANDED_BODY_TOP_PADDING - view.scroll;
            for (GuiDefinition.Module module : visibleModules(view.category)) {
                cy += MODULE_HEIGHT;
                int visibleSettingsHeight = animatedSettingsHeight(module);
                if (visibleSettingsHeight > 0) {
                    int settingY = cy;
                    int visibleBottom = cy + visibleSettingsHeight;
                    for (GuiDefinition.Setting setting : module.settings) {
                        if (!setting.visible()) continue;
                        int height = settingHeight(setting);
                        if (setting == target && isHitTestableSetting(setting, settingY, height, visibleBottom)) {
                            return new HitSetting(view, setting, view.x, settingY, width);
                        }
                        settingY += height;
                    }
                }
                cy += visibleSettingsHeight;
            }
        }
        return null;
    }

    private List<GuiDefinition.Module> visibleModules(GuiDefinition.Category category) {
        return category.modules;
    }

    private boolean isRenderableSetting(GuiDefinition.Setting setting, int y, int height, int visibleBottom) {
        return !(setting instanceof GuiDefinition.SliderSetting) || y + height <= visibleBottom;
    }

    private boolean isHitTestableSetting(GuiDefinition.Setting setting, int y, int height, int visibleBottom) {
        return isRenderableSetting(setting, y, height, visibleBottom);
    }

    private boolean containsSetting(int y, int height, int visibleBottom, double mouseX, double mouseY, int x, int width) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height && mouseY <= visibleBottom;
    }

    private int categoryContentHeight(List<GuiDefinition.Module> modules) {
        int height = 0;
        for (GuiDefinition.Module module : modules) height += moduleHeight(module);
        return Math.max(MODULE_HEIGHT, height);
    }

    private int moduleHeight(GuiDefinition.Module module) {
        return MODULE_HEIGHT + animatedSettingsHeight(module);
    }

    private int animatedSettingsHeight(GuiDefinition.Module module) {
        GuiAnimation animation = moduleAnimations.get(module);
        if (animation == null) return isExpanded(module) ? settingsContentHeight(module) : 0;
        return animation.animatedHeight(settingsContentHeight(module));
    }

    private int settingsContentHeight(GuiDefinition.Module module) {
        int height = 0;
        for (GuiDefinition.Setting setting : module.settings) {
            if (!setting.visible()) continue;
            height += settingHeight(setting);
        }
        return height;
    }

    private int settingHeight(GuiDefinition.Setting setting) {
        if (setting instanceof GuiDefinition.ColorSetting s) return expandedColor == s ? colorPickerSize(CATEGORY_WIDTH) + 32 : SETTING_HEIGHT;
        if (setting instanceof GuiDefinition.SliderSetting) return SLIDER_SETTING_HEIGHT;
        return SETTING_HEIGHT;
    }

    private int categoryBodyHeight(int screenHeight, int contentHeight) {
        int maxHeight = Math.max(80, Math.min(screenHeight - 52, (int) (screenHeight * 0.75f)));
        return Math.min(Math.max(MODULE_HEIGHT, contentHeight), maxHeight);
    }

    private String displayCycleValue(GuiDefinition.CycleSetting setting) {
        return simplifyCycleValue(setting == null ? null : setting.get.get());
    }

    private String simplifyCycleValue(String value) {
        if (value == null) return "";
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "hotbar_left" -> "left";
            case "hotbar_right" -> "right";
            default -> value;
        };
    }

    private int valueTextX(int x, int width, String value) {
        return x + width - CONTROL_X_PADDING - textCache.width(value);
    }

    private int sliderTrackX(int x) {
        return x + CONTROL_X_PADDING;
    }

    private int sliderTrackWidth(int width) {
        return width - CONTROL_X_PADDING * 2;
    }

    private void updateAnimations() {
        long now = System.nanoTime();
        float seconds = Math.min(0.05f, Math.max(0.0f, (now - lastAnimationNanos) / 1_000_000_000.0f));
        lastAnimationNanos = now;
        for (CategoryView view : categoryViews) view.openAnimation.update(seconds);
        moduleAnimations.entrySet().removeIf(entry -> {
            GuiAnimation animation = entry.getValue();
            animation.update(seconds);
            return !animation.isVisible();
        });
    }

    private GuiAnimation moduleAnimation(GuiDefinition.Module module) {
        return moduleAnimations.computeIfAbsent(module, key -> new GuiAnimation(isExpanded(key)));
    }

    private int animatedBodyHeight(CategoryView view, int screenHeight) {
        int fullHeight = categoryBodyHeight(screenHeight, categoryContentHeight(visibleModules(view.category)));
        return view.openAnimation.animatedHeight(fullHeight);
    }

    private boolean containsBody(CategoryView view, double x, double y) {
        int bodyHeight = animatedBodyHeight(view, MinecraftClient.getInstance().getWindow().getScaledHeight());
        return bodyHeight > 0 && UiMath.contains(x, y, view.x - 2, view.y + HEADER_HEIGHT + EXPANDED_BODY_TOP_PADDING, CATEGORY_WIDTH + 4, bodyHeight);
    }

    private boolean containsPanel(CategoryView view, double x, double y) {
        int bodyHeight = animatedBodyHeight(view, MinecraftClient.getInstance().getWindow().getScaledHeight());
        return UiMath.contains(x, y, view.x - 2, view.y, CATEGORY_WIDTH + 4, categoryPanelHeight(bodyHeight));
    }

    private int categoryPanelHeight(int bodyHeight) {
        if (bodyHeight <= 0) return COLLAPSED_PANEL_HEIGHT;
        return Math.max(COLLAPSED_PANEL_HEIGHT, HEADER_HEIGHT + EXPANDED_BODY_TOP_PADDING + 1 + bodyHeight);
    }

    private boolean containsHeader(CategoryView view, double x, double y) {
        int bodyHeight = animatedBodyHeight(view, MinecraftClient.getInstance().getWindow().getScaledHeight());
        int headerHeight = bodyHeight <= 0 ? COLLAPSED_PANEL_HEIGHT : HEADER_HEIGHT;
        return UiMath.contains(x, y, view.x - 2, view.y, CATEGORY_WIDTH + 4, headerHeight);
    }

}
