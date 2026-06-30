package com.yuki.yuki.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.LayeringTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexFormats;

public final class BlockOutlineRenderLayers {
    public static final RenderLayer FILLED = createFilledLayer("block_outline_filled", false);
    public static final RenderLayer PHASE_FILLED = createFilledLayer("block_outline_phase_filled", true);
    public static final RenderLayer PHASE_LINES = createPhaseLinesLayer();

    private BlockOutlineRenderLayers() {}

    private static RenderLayer createPhaseLinesLayer() {
        RenderPipeline pipeline = copyLinesPipeline("block_outline_phase_lines", DepthTestFunction.NO_DEPTH_TEST);
        RenderLayer layer = pipeline == null ? null : createLayer("yuki_block_outline_phase_lines", pipeline);
        return layer == null ? RenderLayers.lines() : layer;
    }

    private static RenderLayer createFilledLayer(String path, boolean phase) {
        DepthTestFunction depthTest = phase ? DepthTestFunction.NO_DEPTH_TEST : preferredDepthTest();
        RenderPipeline pipeline = copyFilledPipeline(path, depthTest);
        if (pipeline == null) return null;
        return createLayer("yuki_" + path, pipeline);
    }

    private static RenderLayer createLayer(String name, RenderPipeline pipeline) {
        try {
            RenderSetup setup = RenderSetup.builder(pipeline)
                .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .translucent()
                .build();
            return RenderPipelineUtil.createLayer(name, setup);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private static RenderPipeline copyLinesPipeline(String path, DepthTestFunction depthTest) {
        RenderPipeline source = RenderPipelines.LINES;
        try {
            return RenderPipelineUtil.copyPipeline(
                "yuki",
                path,
                source,
                source.getVertexShader(),
                source.getFragmentShader(),
                depthTest,
                source.isCull(),
                source.getVertexFormat(),
                source.getVertexFormatMode()
            );
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private static RenderPipeline copyFilledPipeline(String path, DepthTestFunction depthTest) {
        RenderPipeline source = filledSourcePipeline();
        try {
            return RenderPipelineUtil.copyPipeline(
                "yuki",
                path,
                source,
                source.getVertexShader(),
                source.getFragmentShader(),
                depthTest,
                false,
                VertexFormats.POSITION_COLOR,
                VertexFormat.DrawMode.QUADS
            );
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private static RenderPipeline filledSourcePipeline() {
        return RenderPipelines.DEBUG_FILLED_BOX;
    }

    private static DepthTestFunction preferredDepthTest() {
        return DepthTestFunction.LEQUAL_DEPTH_TEST;
    }
}
