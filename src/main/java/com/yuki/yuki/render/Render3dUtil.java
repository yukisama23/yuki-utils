package com.yuki.yuki.render;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

public final class Render3dUtil {
    private Render3dUtil() {}

    public static void writeFilledBox(MatrixStack matrices, VertexConsumer vertexConsumer, Box box, int color) {
        if (matrices == null || vertexConsumer == null || box == null || (color >>> 24) == 0) return;
        MatrixStack.Entry entry = matrices.peek();
        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        quad(vertexConsumer, entry, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, color);
        quad(vertexConsumer, entry, x2, y1, z2, x2, y2, z2, x1, y2, z2, x1, y1, z2, color);
        quad(vertexConsumer, entry, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1, color);
        quad(vertexConsumer, entry, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, color);
        quad(vertexConsumer, entry, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1, color);
        quad(vertexConsumer, entry, x1, y1, z2, x1, y1, z1, x2, y1, z1, x2, y1, z2, color);
    }

    private static void quad(VertexConsumer vertexConsumer, MatrixStack.Entry entry, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int color) {
        vertexConsumer.vertex(entry, x1, y1, z1).color(color);
        vertexConsumer.vertex(entry, x2, y2, z2).color(color);
        vertexConsumer.vertex(entry, x3, y3, z3).color(color);
        vertexConsumer.vertex(entry, x4, y4, z4).color(color);
    }
}
