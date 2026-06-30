package com.yuki.yuki.hud;

import com.yuki.yuki.util.UiMath;

import java.util.function.Predicate;

public final class HudSnapHelper {
    private static final int SNAP_DISTANCE = 6;

    private HudSnapHelper() {}

    public static Result snap(HudElement current, int x, int y, int w, int h, int sw, int sh, Predicate<HudElement> visible) {
        int nx = UiMath.clamp(x, 0, Math.max(0, sw - w));
        int ny = UiMath.clamp(y, 0, Math.max(0, sh - h));
        AxisSnap xs = snapX(current, nx, w, sw, sh, visible);
        AxisSnap ys = snapY(current, ny, h, sw, sh, visible);
        return new Result(
            UiMath.clamp(xs.value(), 0, Math.max(0, sw - w)),
            UiMath.clamp(ys.value(), 0, Math.max(0, sh - h)),
            xs.center(),
            ys.center()
        );
    }

    private static AxisSnap snapX(HudElement current, int x, int w, int sw, int sh, Predicate<HudElement> visible) {
        AxisSnap best = closestX(x, w, sw / 2, true, null);
        for (HudElement element : HudRegistry.getElements()) {
            if (element == current || !visible.test(element)) continue;
            HudGeometry.Bounds bounds = HudGeometry.editorBounds(element, sw, sh);
            best = closestX(x, w, bounds.x(), false, best);
            best = closestX(x, w, bounds.x() + bounds.scaledWidth() / 2, false, best);
            best = closestX(x, w, bounds.x() + bounds.scaledWidth(), false, best);
        }
        return best == null ? new AxisSnap(x, false, Integer.MAX_VALUE) : best;
    }

    private static AxisSnap snapY(HudElement current, int y, int h, int sw, int sh, Predicate<HudElement> visible) {
        AxisSnap best = closestY(y, h, sh / 2, true, null);
        for (HudElement element : HudRegistry.getElements()) {
            if (element == current || !visible.test(element)) continue;
            HudGeometry.Bounds bounds = HudGeometry.editorBounds(element, sw, sh);
            best = closestY(y, h, bounds.y(), false, best);
            best = closestY(y, h, bounds.y() + bounds.scaledHeight() / 2, false, best);
            best = closestY(y, h, bounds.y() + bounds.scaledHeight(), false, best);
        }
        return best == null ? new AxisSnap(y, false, Integer.MAX_VALUE) : best;
    }

    private static AxisSnap closestX(int x, int w, int target, boolean center, AxisSnap best) {
        AxisSnap out = best;
        out = candidate(x, target, center, out);
        out = candidate(x + w / 2, target, center, out, target - w / 2);
        out = candidate(x + w, target, center, out, target - w);
        return out;
    }

    private static AxisSnap closestY(int y, int h, int target, boolean center, AxisSnap best) {
        AxisSnap out = best;
        out = candidate(y, target, center, out);
        out = candidate(y + h / 2, target, center, out, target - h / 2);
        out = candidate(y + h, target, center, out, target - h);
        return out;
    }

    private static AxisSnap candidate(int current, int target, boolean center, AxisSnap best) {
        return candidate(current, target, center, best, target);
    }

    private static AxisSnap candidate(int current, int target, boolean center, AxisSnap best, int snappedValue) {
        int distance = Math.abs(current - target);
        if (distance > SNAP_DISTANCE) return best;
        if (best != null && distance >= best.distance()) return best;
        return new AxisSnap(snappedValue, center, distance);
    }

    public record Result(int x, int y, boolean centerX, boolean centerY) {}
    private record AxisSnap(int value, boolean center, int distance) {}
}
