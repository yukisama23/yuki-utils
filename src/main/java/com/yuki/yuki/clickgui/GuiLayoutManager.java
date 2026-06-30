package com.yuki.yuki.clickgui;

import com.yuki.yuki.config.ConfigManager;
import com.yuki.yuki.util.UiMath;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class GuiLayoutManager {
    private final int categoryWidth;
    private final int panelGap;
    private final int minVisibleDragHandle;
    private final int snapDistance;
    private final Map<String, ConfigManager.ClickGuiCategoryLayout> savedLayouts = new HashMap<>();
    private long interactionSequence;
    private boolean dirty;

    public GuiLayoutManager(int categoryWidth, int panelGap, int minVisibleDragHandle, int snapDistance) {
        this.categoryWidth = categoryWidth;
        this.panelGap = panelGap;
        this.minVisibleDragHandle = minVisibleDragHandle;
        this.snapDistance = snapDistance;
        load();
    }

    public static void resetSavedLayouts() {
        ConfigManager.saveClickGuiCategoryLayouts(Map.of());
    }

    public void load() {
        savedLayouts.clear();
        savedLayouts.putAll(ConfigManager.loadClickGuiCategoryLayouts());
        interactionSequence = 0L;
        for (ConfigManager.ClickGuiCategoryLayout layout : savedLayouts.values()) {
            if (layout != null) interactionSequence = Math.max(interactionSequence, layout.z());
        }
        dirty = false;
    }

    public ConfigManager.ClickGuiCategoryLayout savedLayout(String categoryName) {
        return savedLayouts.get(categoryName);
    }

    public int defaultStartX(int screenWidth, int categoryCount) {
        int totalWidth = categoryCount <= 0 ? 0 : categoryCount * categoryWidth + (categoryCount - 1) * panelGap;
        return Math.max(6, (screenWidth - totalWidth) / 2);
    }

    public int clampX(int screenWidth, int x) {
        return UiMath.clamp(x, minVisibleDragHandle - categoryWidth, screenWidth - minVisibleDragHandle);
    }

    public int clampY(int screenHeight, int y) {
        return UiMath.clamp(y, 0, screenHeight - minVisibleDragHandle);
    }

    public long interactionFor(long savedZ) {
        if (savedZ > 0L) {
            interactionSequence = Math.max(interactionSequence, savedZ);
            return savedZ;
        }
        return nextInteraction();
    }

    public long nextInteraction() {
        return ++interactionSequence;
    }

    public void markDirty(String categoryName, int x, int y, boolean open, long z) {
        if (categoryName == null || categoryName.trim().isEmpty()) return;
        savedLayouts.put(categoryName, new ConfigManager.ClickGuiCategoryLayout(x, y, open, z));
        dirty = true;
    }

    public void saveIfDirty(Collection<CategoryLayoutSnapshot> snapshots) {
        if (!dirty) return;
        savedLayouts.clear();
        if (snapshots != null) {
            for (CategoryLayoutSnapshot snapshot : snapshots) {
                if (snapshot == null || snapshot.name() == null || snapshot.name().trim().isEmpty()) continue;
                savedLayouts.put(snapshot.name(), new ConfigManager.ClickGuiCategoryLayout(snapshot.x(), snapshot.y(), snapshot.open(), snapshot.z()));
            }
        }
        ConfigManager.saveClickGuiCategoryLayouts(savedLayouts);
        dirty = false;
    }

    public int[] snapToCategoryGrid(Collection<CategoryBounds> categories, String movingName, int x, int y) {
        int snappedX = x;
        int snappedY = y;
        if (categories == null) return new int[]{snappedX, snappedY};
        for (CategoryBounds category : categories) {
            if (category == null || category.name().equals(movingName)) continue;
            snappedX = snapTo(snappedX, category.x());
            snappedX = snapTo(snappedX, category.x() + categoryWidth + panelGap);
            snappedX = snapTo(snappedX, category.x() - categoryWidth - panelGap);
            snappedY = snapTo(snappedY, category.y());
        }
        return new int[]{snappedX, snappedY};
    }

    private int snapTo(int value, int target) {
        return Math.abs(value - target) <= snapDistance ? target : value;
    }

    public record CategoryLayoutSnapshot(String name, int x, int y, boolean open, long z) {}
    public record CategoryBounds(String name, int x, int y) {}
}
