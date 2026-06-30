package com.yuki.yuki.util;

public final class ColorUtil {
    private ColorUtil() {}

    public static int lerp(int from, int to, float progress) {
        float p = UiMath.clamp01(progress);
        int a = lerpChannel((from >>> 24) & 255, (to >>> 24) & 255, p);
        int r = lerpChannel((from >>> 16) & 255, (to >>> 16) & 255, p);
        int g = lerpChannel((from >>> 8) & 255, (to >>> 8) & 255, p);
        int b = lerpChannel(from & 255, to & 255, p);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int lerp(int from, int to, double progress) {
        return lerp(from, to, (float) progress);
    }

    private static int lerpChannel(int from, int to, float progress) {
        return Math.round(from + (to - from) * progress);
    }
}
