package com.yuki.yuki.render;

import com.yuki.yuki.util.ColorUtil;
import com.yuki.yuki.util.RoundedRectMath;
import net.minecraft.client.gui.DrawContext;

final class RoundedRectFallbackRenderer {
    private RoundedRectFallbackRenderer() {}

    static void fill(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        for (int row = 0; row < height; row++) {
            int inset = RoundedRectMath.inset(row, height, radius);
            context.fill(x + inset, y + row, x + width - inset, y + row + 1, color);
        }
    }

    static void outline(DrawContext context, int x, int y, int width, int height, int radius, int borderWidth, int color1, int color2) {
        int bw = Math.min(borderWidth, Math.min(width, height) / 2);
        int innerX = bw;
        int innerY = bw;
        int innerWidth = width - bw * 2;
        int innerHeight = height - bw * 2;
        int innerRadius = Math.max(0, radius - bw);
        for (int row = 0; row < height; row++) {
            int outerInset = RoundedRectMath.inset(row, height, radius);
            int outerStart = outerInset;
            int outerEnd = width - outerInset;
            if (innerWidth <= 0 || innerHeight <= 0 || row < innerY || row >= innerY + innerHeight) {
                gradientSegment(context, x, y, width, row, outerStart, outerEnd, color1, color2);
                continue;
            }
            int innerInset = RoundedRectMath.inset(row - innerY, innerHeight, innerRadius);
            int innerStart = innerX + innerInset;
            int innerEnd = innerX + innerWidth - innerInset;
            gradientSegment(context, x, y, width, row, outerStart, Math.min(innerStart, outerEnd), color1, color2);
            gradientSegment(context, x, y, width, row, Math.max(innerEnd, outerStart), outerEnd, color1, color2);
        }
    }

    private static void gradientSegment(DrawContext context, int x, int y, int width, int row, int start, int end, int color1, int color2) {
        if (end <= start) return;
        int total = Math.max(1, width - 1);
        for (int px = start; px < end; px++) {
            context.fill(x + px, y + row, x + px + 1, y + row + 1, ColorUtil.lerp(color1, color2, px / (double) total));
        }
    }
}
