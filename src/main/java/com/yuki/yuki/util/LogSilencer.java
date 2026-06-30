package com.yuki.yuki.util;

public final class LogSilencer {
    private LogSilencer() {}

    public static void muteDandelionInfo() {
        try {
            Class<?> configurator = Class.forName("org.apache.logging.log4j.core.config.Configurator");
            Class<?> levelClass = Class.forName("org.apache.logging.log4j.Level");
            Object warn = levelClass.getMethod("valueOf", String.class).invoke(null, "WARN");
            configurator.getMethod("setLevel", String.class, levelClass).invoke(null, "Dandelion", warn);
            configurator.getMethod("setLevel", String.class, levelClass).invoke(null, "net.azureaaron.dandelion", warn);
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
    }
}
