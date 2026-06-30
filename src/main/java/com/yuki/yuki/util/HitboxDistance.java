package com.yuki.yuki.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class HitboxDistance {
    private static PlayerEntity cachedEyePlayer;
    private static int cachedEyeAge = Integer.MIN_VALUE;
    private static Vec3d cachedEyePos;

    private HitboxDistance() {}

    public static void updateCachedPlayer(PlayerEntity player) {
        if (player == null) {
            clearCache();
            return;
        }
        cacheEyePos(player);
    }

    public static double eyeToEntitySq(PlayerEntity player, Entity entity) {
        if (player == null || entity == null) return Double.MAX_VALUE;
        return pointToBoxSq(eyePos(player), entity.getBoundingBox());
    }

    private static void clearCache() {
        cachedEyePlayer = null;
        cachedEyeAge = Integer.MIN_VALUE;
        cachedEyePos = null;
    }

    private static Vec3d eyePos(PlayerEntity player) {
        if (cachedEyePlayer == player && cachedEyeAge == player.age && cachedEyePos != null) return cachedEyePos;
        return cacheEyePos(player);
    }

    private static double pointToBoxSq(Vec3d point, Box box) {
        if (point == null || box == null) return Double.MAX_VALUE;
        Vec3d closest = closestPoint(box, point);
        double dx = point.x - closest.x;
        double dy = point.y - closest.y;
        double dz = point.z - closest.z;
        return dx * dx + dy * dy + dz * dz;
    }

    private static Vec3d closestPoint(Box box, Vec3d point) {
        return new Vec3d(
            UiMath.clamp(point.x, box.minX, box.maxX),
            UiMath.clamp(point.y, box.minY, box.maxY),
            UiMath.clamp(point.z, box.minZ, box.maxZ)
        );
    }

    private static Vec3d cacheEyePos(PlayerEntity player) {
        cachedEyePlayer = player;
        cachedEyeAge = player.age;
        cachedEyePos = new Vec3d(player.getX(), player.getEyeY(), player.getZ());
        return cachedEyePos;
    }
}
