package com.yuki.yuki.config;

import java.util.Locale;

final class RuntimeKeyBinds {
    private RuntimeKeyBinds() {}

    static int none() {
        return Runtime.KEY_BIND_NONE;
    }

    static int fromMouseButton(int button) {
        return Runtime.MOUSE_BIND_OFFSET - button;
    }

    static boolean isMouseBind(int code) {
        return code <= Runtime.MOUSE_BIND_OFFSET;
    }

    static int toMouseButton(int code) {
        return Runtime.MOUSE_BIND_OFFSET - code;
    }

    static String moduleId(String moduleName) {
        if (moduleName == null) return "";
        return moduleName.trim()
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "_")
            .replaceAll("^_+|_+$", "");
    }

    static String configKey(String moduleName) {
        String id = normalizeId(moduleId(moduleName));
        return id.isEmpty() ? "keyBind" : "keyBind_" + id;
    }

    static int clickGui() {
        return Runtime.KEY_BINDS.clickGuiKeyBind;
    }

    static int get(String moduleName) {
        String id = normalizeId(moduleId(moduleName));
        if (Runtime.CLICK_GUI_KEY_BIND_ID.equals(id)) return Runtime.KEY_BINDS.clickGuiKeyBind;
        Integer value = Runtime.moduleKeyBinds.get(id);
        return value == null ? Runtime.KEY_BIND_NONE : value;
    }

    static void set(String moduleName, int code) {
        String id = normalizeId(moduleId(moduleName));
        if (id.isEmpty()) return;
        if (Runtime.CLICK_GUI_KEY_BIND_ID.equals(id)) {
            Runtime.KEY_BINDS.clickGuiKeyBind = code;
            return;
        }
        if (code == Runtime.KEY_BIND_NONE) Runtime.moduleKeyBinds.remove(id);
        else Runtime.moduleKeyBinds.put(id, code);
    }

    static String normalizeId(String id) {
        if (id == null) return "";
        String normalized = id.trim().toLowerCase(Locale.ROOT);
        return normalized.equals("clickgui") ? Runtime.CLICK_GUI_KEY_BIND_ID : normalized;
    }

    static String name(int code) {
        if (code == Runtime.KEY_BIND_NONE) return "None";
        if (isMouseBind(code)) return mouseName(toMouseButton(code));
        if (code >= 32 && code <= 96) return String.valueOf((char) code).toUpperCase(Locale.ROOT);
        return specialKeyName(code);
    }

    private static String mouseName(int button) {
        return switch (button) {
            case 2 -> "Mouse Middle";
            case 3 -> "Mouse 4";
            case 4 -> "Mouse 5";
            default -> "Mouse " + (button + 1);
        };
    }

    private static String specialKeyName(int code) {
        return switch (code) {
            case 256 -> "Escape";
            case 257 -> "Enter";
            case 258 -> "Tab";
            case 259 -> "Backspace";
            case 260 -> "Insert";
            case 261 -> "Delete";
            case 262 -> "Right";
            case 263 -> "Left";
            case 264 -> "Down";
            case 265 -> "Up";
            case 280 -> "Caps Lock";
            case 290 -> "F1";
            case 291 -> "F2";
            case 292 -> "F3";
            case 293 -> "F4";
            case 294 -> "F5";
            case 295 -> "F6";
            case 296 -> "F7";
            case 297 -> "F8";
            case 298 -> "F9";
            case 299 -> "F10";
            case 300 -> "F11";
            case 301 -> "F12";
            case 340 -> "LShift";
            case 341 -> "LCtrl";
            case 342 -> "LAlt";
            case 343 -> "LSuper";
            case 344 -> "RShift";
            case 345 -> "RCtrl";
            case 346 -> "RAlt";
            case 347 -> "RSuper";
            default -> "Key " + code;
        };
    }
}
