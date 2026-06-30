package com.yuki.yuki.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.util.Identifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class RenderPipelineUtil {
    private static Constructor<?> pipelineConstructor;
    private static Method renderLayerFactory;
    private static Method registerMethod;
    private static boolean constructorResolved;
    private static boolean layerFactoryResolved;
    private static boolean registerResolved;

    private RenderPipelineUtil() {}

    public static RenderPipeline copyPipeline(
        String namespace,
        String path,
        RenderPipeline source,
        Object vertexShader,
        Object fragmentShader,
        DepthTestFunction depthTest,
        boolean cull,
        VertexFormat vertexFormat,
        VertexFormat.DrawMode drawMode
    ) throws ReflectiveOperationException {
        Constructor<?> constructor = pipelineConstructor();
        RenderPipeline pipeline = (RenderPipeline) constructor.newInstance(
            Identifier.of(namespace, path),
            vertexShader,
            fragmentShader,
            source.getShaderDefines(),
            source.getSamplers(),
            source.getUniforms(),
            source.getBlendFunction(),
            depthTest,
            source.getPolygonMode(),
            cull,
            source.isWriteColor(),
            source.isWriteAlpha(),
            false,
            source.getColorLogic(),
            vertexFormat,
            drawMode,
            source.getDepthBiasScaleFactor(),
            source.getDepthBiasConstant(),
            source.getSortKey()
        );
        return register(pipeline);
    }

    public static RenderLayer createLayer(String name, RenderSetup setup) throws ReflectiveOperationException {
        return (RenderLayer) renderLayerFactory().invoke(null, name, setup);
    }

    public static RenderPipeline register(RenderPipeline pipeline) {
        Method method = registerMethod();
        if (method == null) return pipeline;
        try {
            return (RenderPipeline) method.invoke(null, pipeline);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return pipeline;
        }
    }

    private static Constructor<?> pipelineConstructor() throws NoSuchMethodException {
        if (!constructorResolved) {
            pipelineConstructor = findPipelineConstructor();
            constructorResolved = true;
        }
        if (pipelineConstructor == null) throw new NoSuchMethodException("RenderPipeline constructor");
        return pipelineConstructor;
    }

    private static Constructor<?> findPipelineConstructor() {
        for (Constructor<?> constructor : RenderPipeline.class.getDeclaredConstructors()) {
            if (constructor.getParameterCount() != 19) continue;
            constructor.setAccessible(true);
            return constructor;
        }
        return null;
    }

    private static Method renderLayerFactory() throws NoSuchMethodException {
        if (!layerFactoryResolved) {
            renderLayerFactory = findRenderLayerFactory();
            layerFactoryResolved = true;
        }
        if (renderLayerFactory == null) throw new NoSuchMethodException("RenderLayer factory");
        return renderLayerFactory;
    }

    private static Method findRenderLayerFactory() {
        for (Method method : RenderLayer.class.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) continue;
            if (!RenderLayer.class.isAssignableFrom(method.getReturnType())) continue;
            Class<?>[] types = method.getParameterTypes();
            if (types.length != 2 || types[0] != String.class || types[1] != RenderSetup.class) continue;
            method.setAccessible(true);
            return method;
        }
        return null;
    }

    private static Method registerMethod() {
        if (!registerResolved) {
            registerMethod = findRegisterMethod();
            registerResolved = true;
        }
        return registerMethod;
    }

    private static Method findRegisterMethod() {
        for (Method method : RenderPipelines.class.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) continue;
            if (!RenderPipeline.class.isAssignableFrom(method.getReturnType())) continue;
            Class<?>[] types = method.getParameterTypes();
            if (types.length != 1 || !RenderPipeline.class.isAssignableFrom(types[0])) continue;
            method.setAccessible(true);
            return method;
        }
        return null;
    }
}
