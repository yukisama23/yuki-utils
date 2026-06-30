package com.yuki.yuki.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.yuki.yuki.mixin.DrawContextAccessor;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;

public final class CustomTooltipRoundRectRenderer {
    private static final RenderPipeline PIPELINE = createPipelineOrNull();
    private static boolean shaderQueueBroken;

    private CustomTooltipRoundRectRenderer() {}

    public static void drawFill(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        if (!enqueueShaderDraw(context, x, y, width, height, radius, 0, color, color)) {
            RoundedRectFallbackRenderer.fill(context, x, y, width, height, radius, color);
        }
    }

    public static void drawBorder(DrawContext context, int x, int y, int width, int height, int radius, float borderWidth, int color1, int color2) {
        if (borderWidth <= 0.0f) return;
        if (!enqueueShaderDraw(context, x, y, width, height, radius, borderWidth, color1, color2)) {
            RoundedRectFallbackRenderer.outline(context, x, y, width, height, radius, Math.max(1, Math.round(borderWidth)), color1, color2);
        }
    }

    private static boolean enqueueShaderDraw(DrawContext context, int x, int y, int width, int height, int radius, float borderWidth, int color1, int color2) {
        if (PIPELINE == null || shaderQueueBroken || width <= 0 || height <= 0) return false;
        try {
            int r = Math.min(radius, width / 2);
            float border = Math.min(borderWidth, Math.min(width, height) / 2.0f);
            int encodedBorder = border <= 0.0f ? 0 : Math.max(1, Math.round(border * 2.0f));
            Matrix3x2f pose = new Matrix3x2f(context.getMatrices());
            ScreenRect bounds = new ScreenRect(x, y, width, height);
            ((DrawContextAccessor) context).yuki$getState().addSimpleElement(new RoundRectState(PIPELINE, TextureSetup.empty(), pose, x, y, x + width, y + height, color1, color2, color2, color1, r, encodedBorder, bounds));
            return true;
        } catch (RuntimeException ignored) {
            shaderQueueBroken = true;
            return false;
        }
    }

    private static RenderPipeline createPipelineOrNull() {
        RenderPipeline source = RenderPipelines.GUI;
        try {
            return RenderPipelineUtil.copyPipeline(
                "yuki",
                "pipeline/custom_tooltip_round_rect",
                source,
                Identifier.of("yuki", "core/custom_tooltip_round_rect"),
                Identifier.of("yuki", "core/custom_tooltip_round_rect"),
                DepthTestFunction.NO_DEPTH_TEST,
                false,
                VertexFormats.POSITION_TEXTURE_COLOR_LIGHT,
                VertexFormat.DrawMode.QUADS
            );
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private static final class RoundRectState implements SimpleGuiElementRenderState {
        private final RenderPipeline pipeline;
        private final TextureSetup textureSetup;
        private final Matrix3x2fc pose;
        private final int x0;
        private final int y0;
        private final int x1;
        private final int y1;
        private final int color0;
        private final int color1;
        private final int color2;
        private final int color3;
        private final int radius;
        private final int border;
        private final ScreenRect bounds;

        private RoundRectState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2fc pose, int x0, int y0, int x1, int y1, int color0, int color1, int color2, int color3, int radius, int border, ScreenRect bounds) {
            this.pipeline = pipeline;
            this.textureSetup = textureSetup;
            this.pose = pose;
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            this.color0 = color0;
            this.color1 = color1;
            this.color2 = color2;
            this.color3 = color3;
            this.radius = radius;
            this.border = border;
            this.bounds = bounds;
        }

        @Override
        public RenderPipeline pipeline() {
            return pipeline;
        }

        @Override
        public TextureSetup textureSetup() {
            return textureSetup;
        }

        @Override
        public ScreenRect scissorArea() {
            return null;
        }

        @Override
        public ScreenRect bounds() {
            return bounds;
        }

        @Override
        public void setupVertices(VertexConsumer vertices) {
            vertex(vertices, x0, y0, -0.5f, -0.5f, color0);
            vertex(vertices, x1, y0, 0.5f, -0.5f, color1);
            vertex(vertices, x1, y1, 0.5f, 0.5f, color2);
            vertex(vertices, x0, y1, -0.5f, 0.5f, color3);
        }

        private void vertex(VertexConsumer vertices, int x, int y, float u, float v, int color) {
            vertices.vertex(pose, x, y).texture(u, v).color(color).light(radius, border);
        }
    }
}
