package com.yuki.yuki.clickgui;

import java.util.Collection;

final class CategoryDragController {
    private final int threshold;
    private CategoryView pending;
    private CategoryView dragging;
    private int startMouseX;
    private int startMouseY;
    private int offsetX;
    private int offsetY;

    CategoryDragController(int threshold) {
        this.threshold = Math.max(0, threshold);
    }

    boolean active() {
        return pending != null || dragging != null;
    }

    boolean dragging() {
        return dragging != null;
    }

    void begin(CategoryView view, double mouseX, double mouseY) {
        pending = view;
        dragging = null;
        startMouseX = (int) Math.round(mouseX);
        startMouseY = (int) Math.round(mouseY);
        offsetX = startMouseX - view.x;
        offsetY = startMouseY - view.y;
    }

    CategoryView update(double mouseX, double mouseY, GuiLayoutManager layout, Collection<GuiLayoutManager.CategoryBounds> bounds, int screenWidth, int screenHeight) {
        int mx = (int) Math.round(mouseX);
        int my = (int) Math.round(mouseY);
        if (dragging == null) {
            if (Math.abs(mx - startMouseX) < threshold && Math.abs(my - startMouseY) < threshold) return null;
            dragging = pending;
            pending = null;
        }
        return move(mx, my, layout, bounds, screenWidth, screenHeight);
    }

    void clear() {
        pending = null;
        dragging = null;
    }

    private CategoryView move(int mouseX, int mouseY, GuiLayoutManager layout, Collection<GuiLayoutManager.CategoryBounds> bounds, int screenWidth, int screenHeight) {
        if (dragging == null) return null;
        int nextX = layout.clampX(screenWidth, mouseX - offsetX);
        int nextY = layout.clampY(screenHeight, mouseY - offsetY);
        int[] snapped = layout.snapToCategoryGrid(bounds, dragging.category.name, nextX, nextY);
        nextX = layout.clampX(screenWidth, snapped[0]);
        nextY = layout.clampY(screenHeight, snapped[1]);
        if (dragging.x == nextX && dragging.y == nextY) return null;
        dragging.x = nextX;
        dragging.y = nextY;
        return dragging;
    }
}
