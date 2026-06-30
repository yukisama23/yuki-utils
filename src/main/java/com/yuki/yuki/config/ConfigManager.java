package com.yuki.yuki.config;

import com.yuki.yuki.Yuki;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ConfigManager {
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("yuki.json");

    private static final Gson DIRECT_GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final DateTimeFormatter BACKUP_FMT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String CLICK_GUI_LAYOUTS_KEY = "clickGuiCategoryLayouts";
    private static final String KEY_BIND_PREFIX = "keyBind_";

    private static final Set<String> SETTINGS_RESET_PRESERVED_KEYS = Set.of(
        "clickGuiColor",
        "clickGuiColor2",
        "blockOutlineColor1",
        "blockOutlineColor2",
        "blockOutlineFilledColor",
        "customTooltipBorderColor1",
        "customTooltipBorderColor2",
        "customTooltipBackgroundColor",
        "reachDisplayHudX",
        "reachDisplayHudY",
        "reachDisplayHudScale",
        "effectHudX",
        "effectHudY",
        "effectHudScale",
        "totemTweaksHudX",
        "totemTweaksHudY",
        "totemTweaksHudScale",
        "totemTweaksAlertHudX",
        "totemTweaksAlertHudY",
        "totemTweaksAlertHudScale",
        "armorHudX",
        "armorHudY",
        "armorHudScale",
        "armorHudOffhandX",
        "armorHudOffhandY",
        "armorHudOffhandScale",
        CLICK_GUI_LAYOUTS_KEY
    );

    private ConfigManager() {}

    public static void safeLoad() {
        try {
            if (!Files.exists(CONFIG_PATH)) return;
            JsonObject obj = JsonParser.parseString(Files.readString(CONFIG_PATH)).getAsJsonObject();
            cleanupConfig(obj);
            writeJsonAtomic(CONFIG_PATH, obj);
        } catch (IOException | RuntimeException t) {
            Yuki.LOGGER.warn("Failed to load config, resetting to defaults", t);
            backupBrokenConfig();
        }
    }

    private static JsonObject readConfigObject() throws IOException {
        if (!Files.exists(CONFIG_PATH)) return new JsonObject();
        return JsonParser.parseString(Files.readString(CONFIG_PATH)).getAsJsonObject();
    }

    private static boolean writeConfigValue(String key, JsonPrimitive value) {
        if (key == null || key.isBlank()) return false;
        try {
            JsonObject obj = readConfigObject();
            setConfigValue(obj, key, value);
            writeAndReload(obj);
            return true;
        } catch (IOException | RuntimeException t) {
            Yuki.LOGGER.warn("Failed to set config value", t);
            return false;
        }
    }

    private static void setConfigValue(JsonObject obj, String key, JsonElement value) {
        String[] path = Config.pathFor(key);
        if (path == null) {
            obj.add(key, value);
            return;
        }

        JsonObject target = obj;
        for (int i = 0; i < path.length - 1; i++) {
            JsonElement existing = target.get(path[i]);
            JsonObject next = existing != null && existing.isJsonObject()
                ? existing.getAsJsonObject()
                : new JsonObject();

            if (existing == null || !existing.isJsonObject()) {
                target.add(path[i], next);
            }
            target = next;
        }
        target.add(path[path.length - 1], value);
        obj.remove(key);
    }

    private static JsonElement getConfigValue(JsonObject obj, String key) {
        String[] path = Config.pathFor(key);
        if (path == null) return obj.get(key);

        JsonObject target = obj;
        for (int i = 0; i < path.length - 1; i++) {
            JsonElement next = target.get(path[i]);
            if (next == null || !next.isJsonObject()) return obj.get(key);
            target = next.getAsJsonObject();
        }

        JsonElement value = target.get(path[path.length - 1]);
        return value == null ? obj.get(key) : value;
    }

    private static void cleanupConfig(JsonObject obj) {
        if (obj == null) return;

        Config cfg = Config.fromJson(obj, DIRECT_GSON);
        JsonObject clean = DIRECT_GSON.toJsonTree(cfg).getAsJsonObject();
        copyClickGuiLayouts(obj, clean);
        copyKeyBinds(obj, clean);

        obj.entrySet().clear();
        for (Map.Entry<String, JsonElement> entry : clean.entrySet()) {
            obj.add(entry.getKey(), entry.getValue());
        }
        Config.removeLegacyKeys(obj);
    }

    private static void copyClickGuiLayouts(JsonObject source, JsonObject target) {
        JsonElement layouts = source.get(CLICK_GUI_LAYOUTS_KEY);
        if (layouts != null) target.add(CLICK_GUI_LAYOUTS_KEY, layouts.deepCopy());
    }

    private static void copyKeyBinds(JsonObject source, JsonObject target) {
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            String key = entry.getKey();
            if (key != null && key.startsWith(KEY_BIND_PREFIX)) {
                target.add(key, entry.getValue().deepCopy());
            }
        }
    }

    private static void backupBrokenConfig() {
        try {
            backup(CONFIG_PATH, "broken");
            Files.deleteIfExists(CONFIG_PATH);
        } catch (IOException | RuntimeException t) {
            Yuki.LOGGER.warn("Failed to backup/remove broken config", t);
        }
    }

    private static void backup(Path src, String tag) throws IOException {
        if (!Files.exists(src)) return;
        Files.createDirectories(src.getParent());
        String ts = LocalDateTime.now().format(BACKUP_FMT);
        Path dst = src.resolveSibling(src.getFileName() + "." + tag + "." + ts + ".bak");
        Files.move(src, dst, StandardCopyOption.REPLACE_EXISTING);
    }

    private static void writeAndReload(JsonObject obj) throws IOException {
        writeJsonAtomic(CONFIG_PATH, obj);
        Runtime.loadFromDisk(CONFIG_PATH);
    }

    private static void writeJsonAtomic(Path dst, JsonObject obj) throws IOException {
        Files.createDirectories(dst.getParent());
        Path tmp = dst.resolveSibling(dst.getFileName() + ".tmp");
        Files.writeString(tmp, DIRECT_GSON.toJson(obj));
        try {
            Files.move(tmp, dst, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(tmp, dst, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void resetAll() {
        try {
            writeAndReload(DIRECT_GSON.toJsonTree(new Config()).getAsJsonObject());
        } catch (IOException | RuntimeException t) {
            Yuki.LOGGER.warn("Failed to reset config", t);
        }
    }

    public static void resetSettings() {
        try {
            JsonObject current = readConfigObject();
            JsonObject obj = DIRECT_GSON.toJsonTree(new Config()).getAsJsonObject();
            for (String key : SETTINGS_RESET_PRESERVED_KEYS) {
                JsonElement value = getConfigValue(current, key);
                if (value != null) setConfigValue(obj, key, value.deepCopy());
            }
            writeAndReload(obj);
        } catch (IOException | RuntimeException t) {
            Yuki.LOGGER.warn("Failed to reset settings", t);
        }
    }

    public record ClickGuiCategoryLayout(int x, int y, boolean open, long z) {}

    public static Map<String, ClickGuiCategoryLayout> loadClickGuiCategoryLayouts() {
        HashMap<String, ClickGuiCategoryLayout> out = new HashMap<>();
        try {
            JsonObject obj = readConfigObject();
            JsonElement root = obj.get(CLICK_GUI_LAYOUTS_KEY);
            if (root == null || !root.isJsonObject()) return out;

            JsonObject layouts = root.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : layouts.entrySet()) {
                if (entry.getKey() == null || entry.getKey().isBlank()) continue;
                JsonElement value = entry.getValue();
                if (value == null || !value.isJsonObject()) continue;

                JsonObject layout = value.getAsJsonObject();
                if (!layout.has("x") || !layout.has("y")) continue;

                int x = layout.get("x").getAsInt();
                int y = layout.get("y").getAsInt();
                boolean open = layout.has("open") && layout.get("open").getAsBoolean();
                long z = layout.has("z") ? layout.get("z").getAsLong() : 0L;
                out.put(entry.getKey(), new ClickGuiCategoryLayout(x, y, open, z));
            }
        } catch (IOException | RuntimeException t) {
            Yuki.LOGGER.warn("Failed to load click gui layout", t);
        }
        return out;
    }

    public static void saveClickGuiCategoryLayouts(Map<String, ClickGuiCategoryLayout> states) {
        try {
            JsonObject obj = readConfigObject();
            if (states == null || states.isEmpty()) {
                obj.remove(CLICK_GUI_LAYOUTS_KEY);
            } else {
                obj.add(CLICK_GUI_LAYOUTS_KEY, toJsonLayouts(states));
            }
            writeJsonAtomic(CONFIG_PATH, obj);
        } catch (IOException | RuntimeException t) {
            Yuki.LOGGER.warn("Failed to save click gui layout", t);
        }
    }

    private static JsonObject toJsonLayouts(Map<String, ClickGuiCategoryLayout> states) {
        JsonObject layouts = new JsonObject();
        for (Map.Entry<String, ClickGuiCategoryLayout> entry : states.entrySet()) {
            String name = entry.getKey();
            ClickGuiCategoryLayout state = entry.getValue();
            if (name == null || name.isBlank() || state == null) continue;

            JsonObject layout = new JsonObject();
            layout.addProperty("x", state.x());
            layout.addProperty("y", state.y());
            layout.addProperty("open", state.open());
            layout.addProperty("z", state.z());
            layouts.add(name, layout);
        }
        return layouts;
    }

    public static void saveHud() {
        try {
            JsonObject obj = readConfigObject();
            for (HudPlacement placement : hudPlacements()) {
                saveHudPlacement(obj, placement);
            }
            writeAndReload(obj);
        } catch (IOException | RuntimeException t) {
            Yuki.LOGGER.warn("Failed to save hud", t);
        }
    }

    private static HudPlacement[] hudPlacements() {
        return new HudPlacement[] {
            new HudPlacement("reachDisplayHud", Runtime.getReachDisplayHudX(), Runtime.getReachDisplayHudY(), Runtime.getReachDisplayHudScale()),
            new HudPlacement("effectHud", Runtime.getEffectHudX(), Runtime.getEffectHudY(), Runtime.getEffectHudScale()),
            new HudPlacement("totemTweaksHud", Runtime.getTotemTweaksHudX(), Runtime.getTotemTweaksHudY(), Runtime.getTotemTweaksHudScale()),
            new HudPlacement("totemTweaksAlertHud", Runtime.getTotemTweaksAlertHudX(), Runtime.getTotemTweaksAlertHudY(), Runtime.getTotemTweaksAlertHudScale()),
            new HudPlacement("armorHud", Runtime.getArmorHudX(), Runtime.getArmorHudY(), Runtime.getArmorHudScale()),
            new HudPlacement("armorHudOffhand", Runtime.getArmorHudOffhandX(), Runtime.getArmorHudOffhandY(), Runtime.getArmorHudOffhandScale())
        };
    }

    private static void saveHudPlacement(JsonObject obj, HudPlacement placement) {
        setConfigValue(obj, placement.prefix() + "X", new JsonPrimitive(placement.x()));
        setConfigValue(obj, placement.prefix() + "Y", new JsonPrimitive(placement.y()));
        setConfigValue(obj, placement.prefix() + "Scale", new JsonPrimitive(placement.scale()));
    }

    private record HudPlacement(String prefix, int x, int y, float scale) {}

    public static boolean setBoolean(String key, boolean v) {
        return writeConfigValue(key, new JsonPrimitive(v));
    }

    public static boolean setInt(String key, int v) {
        return writeConfigValue(key, new JsonPrimitive(v));
    }

    public static boolean setFloat(String key, float v) {
        return writeConfigValue(key, new JsonPrimitive(v));
    }

    public static boolean setString(String key, String v) {
        return writeConfigValue(key, new JsonPrimitive(v == null ? "" : v));
    }
}
