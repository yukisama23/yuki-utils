package com.yuki.yuki.clickgui;

final class CategoryView {
    final GuiDefinition.Category category;
    int x;
    int y;
    int scroll;
    boolean open;
    final GuiAnimation openAnimation;
    long lastInteracted;

    CategoryView(GuiDefinition.Category category, int x, int y, boolean open, long lastInteracted) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.scroll = 0;
        this.open = open;
        this.openAnimation = new GuiAnimation(open);
        this.lastInteracted = lastInteracted;
    }
}
