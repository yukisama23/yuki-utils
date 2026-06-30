package com.yuki.yuki.util;

public final class RoundedRectMath {
    private RoundedRectMath() {}

    public static int inset(int row, int height, int radius) {
        if (radius <= 0) return 0;
        if (row < radius) return insetForDistance(radius - row - 0.5, radius);
        if (row >= height - radius) return insetForDistance(row - (height - radius) + 0.5, radius);
        return 0;
    }

    private static int insetForDistance(double distance, int radius) {
        double inside = Math.max(0.0, radius * radius - distance * distance);
        return Math.max(0, (int) Math.ceil(radius - Math.sqrt(inside)));
    }
}
