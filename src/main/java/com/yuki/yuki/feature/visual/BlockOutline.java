package com.yuki.yuki.feature.visual;

import com.yuki.yuki.config.Runtime;
import com.yuki.yuki.render.BlockOutlineRenderLayers;
import com.yuki.yuki.render.Render3dUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.state.OutlineRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.joml.Matrix4fc;

public final class BlockOutline {
    private static final double BOX_EPSILON = 1.0E-5;
    private static BlockPos cachedPos;
    private static Box previousBox;
    private static Box targetBox;
    private static long animationTick = Long.MIN_VALUE;

    private BlockOutline() {}

    public static void render(MatrixStack matrices, VertexConsumer vertexConsumer, double cameraX, double cameraY, double cameraZ, OutlineRenderState state) {
        if (matrices == null || vertexConsumer == null || state == null) {
            return;
        }

        VoxelShape shape = state.shape();
        if (shape == null || shape.isEmpty()) {
            reset();
            return;
        }

        BlockPos pos = state.pos();
        if (pos == null) {
            reset();
            return;
        }

        VoxelShape renderShape = smoothAnimatedShape(pos, shape);
        double offsetX = pos.getX() - cameraX;
        double offsetY = pos.getY() - cameraY;
        double offsetZ = pos.getZ() - cameraZ;
        boolean phase = Runtime.isBlockOutlinePhaseEnabled();
        if (Runtime.isBlockOutlineFilledEnabled()) renderFilled(matrices, renderShape, offsetX, offsetY, offsetZ, phase);
        if (phase) {
            renderPhaseOutline(matrices, renderShape, offsetX, offsetY, offsetZ);
            return;
        }
        renderOutline(matrices, vertexConsumer, renderShape, offsetX, offsetY, offsetZ);
    }

    private static VoxelShape smoothAnimatedShape(BlockPos pos, VoxelShape shape) {
        if (shape == null || shape.isEmpty()) {
            reset();
            return shape;
        }

        Box currentBox = shape.getBoundingBox();
        long worldTime = worldTime();
        if (cachedPos == null || !cachedPos.equals(pos) || previousBox == null || targetBox == null) {
            cachedPos = pos.toImmutable();
            previousBox = currentBox;
            targetBox = currentBox;
            animationTick = worldTime;
            return shape;
        }

        if (!sameBox(currentBox, targetBox)) {
            previousBox = displayedBox(worldTime);
            targetBox = currentBox;
            animationTick = worldTime;
        }

        if (worldTime != animationTick || sameBox(previousBox, targetBox)) {
            return shape;
        }

        float progress = renderTickProgress();
        Box lerped = lerpBox(previousBox, targetBox, smoothStep(progress));
        return VoxelShapes.cuboid(lerped);
    }

    private static Box displayedBox(long worldTime) {
        if (previousBox == null || targetBox == null) {
            return targetBox;
        }
        if (worldTime != animationTick || sameBox(previousBox, targetBox)) {
            return targetBox;
        }
        return lerpBox(previousBox, targetBox, smoothStep(renderTickProgress()));
    }

    private static float renderTickProgress() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return 1.0f;
        }
        return MathHelper.clamp(client.getRenderTickCounter().getTickProgress(false), 0.0f, 1.0f);
    }

    private static long worldTime() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.world == null) {
            return Long.MIN_VALUE;
        }
        return client.world.getTime();
    }

    private static Box lerpBox(Box from, Box to, float progress) {
        return new Box(
            MathHelper.lerp(progress, from.minX, to.minX),
            MathHelper.lerp(progress, from.minY, to.minY),
            MathHelper.lerp(progress, from.minZ, to.minZ),
            MathHelper.lerp(progress, from.maxX, to.maxX),
            MathHelper.lerp(progress, from.maxY, to.maxY),
            MathHelper.lerp(progress, from.maxZ, to.maxZ)
        );
    }

    private static float smoothStep(float progress) {
        float t = MathHelper.clamp(progress, 0.0f, 1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private static boolean sameBox(Box a, Box b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return Math.abs(a.minX - b.minX) < BOX_EPSILON
            && Math.abs(a.minY - b.minY) < BOX_EPSILON
            && Math.abs(a.minZ - b.minZ) < BOX_EPSILON
            && Math.abs(a.maxX - b.maxX) < BOX_EPSILON
            && Math.abs(a.maxY - b.maxY) < BOX_EPSILON
            && Math.abs(a.maxZ - b.maxZ) < BOX_EPSILON;
    }

    public static void reset() {
        cachedPos = null;
        previousBox = null;
        targetBox = null;
        animationTick = Long.MIN_VALUE;
    }

    private static void renderFilled(MatrixStack matrices, VoxelShape shape, double offsetX, double offsetY, double offsetZ, boolean phase) {
        int color = Runtime.getBlockOutlineFilledArgb();
        if ((color >>> 24) == 0) return;
        RenderLayer layer = phase ? BlockOutlineRenderLayers.PHASE_FILLED : BlockOutlineRenderLayers.FILLED;
        if (layer == null) return;
        BufferBuilder buffer = Tessellator.getInstance().begin(layer.getDrawMode(), layer.getVertexFormat());
        renderFilledShape(matrices, buffer, shape, offsetX, offsetY, offsetZ, color);
        BuiltBuffer built = buffer.endNullable();
        if (built != null) {
            layer.draw(built);
        }
    }

    private static void renderFilledShape(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, int color) {
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> renderFilledBox(
            matrices,
            vertexConsumer,
            offsetX + minX, offsetY + minY, offsetZ + minZ,
            offsetX + maxX, offsetY + maxY, offsetZ + maxZ,
            color
        ));
    }

    private static void renderFilledBox(MatrixStack matrices, VertexConsumer vertexConsumer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int color) {
        Render3dUtil.writeFilledBox(matrices, vertexConsumer, new Box(minX, minY, minZ, maxX, maxY, maxZ), color);
    }

    private static void renderOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ) {
        VertexConsumer gradient = new GradientLineVertexConsumer(vertexConsumer, Runtime.getBlockOutlineColor1(), Runtime.getBlockOutlineColor2(), offsetX, offsetY, offsetZ);
        VertexRendering.drawOutline(matrices, gradient, shape, offsetX, offsetY, offsetZ, 0xFFFFFFFF, Runtime.getBlockOutlineRenderWidth());
    }

    private static void renderPhaseOutline(MatrixStack matrices, VoxelShape shape, double offsetX, double offsetY, double offsetZ) {
        RenderLayer layer = BlockOutlineRenderLayers.PHASE_LINES;
        BufferBuilder buffer = Tessellator.getInstance().begin(layer.getDrawMode(), layer.getVertexFormat());
        renderOutline(matrices, buffer, shape, offsetX, offsetY, offsetZ);
        BuiltBuffer built = buffer.endNullable();
        if (built != null) {
            layer.draw(built);
        }
    }

    private static int mix(int color1, int color2, float t) {
        float clamped = MathHelper.clamp(t, 0.0f, 1.0f);
        int a1 = color1 >>> 24;
        int r1 = (color1 >>> 16) & 0xFF;
        int g1 = (color1 >>> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int a2 = color2 >>> 24;
        int r2 = (color2 >>> 16) & 0xFF;
        int g2 = (color2 >>> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int a = MathHelper.clamp(Math.round(a1 + (a2 - a1) * clamped), 0, 255);
        int r = MathHelper.clamp(Math.round(r1 + (r2 - r1) * clamped), 0, 255);
        int g = MathHelper.clamp(Math.round(g1 + (g2 - g1) * clamped), 0, 255);
        int b = MathHelper.clamp(Math.round(b1 + (b2 - b1) * clamped), 0, 255);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static final class GradientLineVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final int color1;
        private final int color2;
        private final double offsetX;
        private final double offsetY;
        private final double offsetZ;
        private float lastX;
        private float lastY;
        private float lastZ;

        private GradientLineVertexConsumer(VertexConsumer delegate, int color1, int color2, double offsetX, double offsetY, double offsetZ) {
            this.delegate = delegate;
            this.color1 = color1;
            this.color2 = color2;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
        }

        @Override
        public VertexConsumer vertex(float x, float y, float z) {
            remember(x, y, z);
            delegate.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer vertex(MatrixStack.Entry matrix, float x, float y, float z) {
            remember(x, y, z);
            delegate.vertex(matrix, x, y, z);
            return this;
        }

        @Override
        public VertexConsumer vertex(Matrix4fc matrix, float x, float y, float z) {
            remember(x, y, z);
            delegate.vertex(matrix, x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int argb) {
            delegate.color(gradientColor());
            return this;
        }

        @Override
        public VertexConsumer color(int red, int green, int blue, int alpha) {
            delegate.color(gradientColor());
            return this;
        }

        @Override
        public VertexConsumer texture(float u, float v) {
            delegate.texture(u, v);
            return this;
        }

        @Override
        public VertexConsumer overlay(int u, int v) {
            delegate.overlay(u, v);
            return this;
        }

        @Override
        public VertexConsumer light(int u, int v) {
            delegate.light(u, v);
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            delegate.normal(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer lineWidth(float width) {
            delegate.lineWidth(width);
            return this;
        }

        private void remember(float x, float y, float z) {
            lastX = x;
            lastY = y;
            lastZ = z;
        }

        private int gradientColor() {
            float localX = (float) (lastX - offsetX);
            float localY = (float) (lastY - offsetY);
            float localZ = (float) (lastZ - offsetZ);
            return mix(color1, color2, (localX + localY + localZ) / 3.0f);
        }
    }
}
