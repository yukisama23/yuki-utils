package com.yuki.yuki.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.EnumSet;

public class LegacyText {
    public static Text parse(String s) {
        if (s == null || s.isEmpty()) return Text.empty();
        MutableText out = Text.empty();
        StringBuilder buf = new StringBuilder();
        Formatting color = null;
        EnumSet<Formatting> formats = EnumSet.noneOf(Formatting.class);
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((ch == '&' || ch == '§') && i + 1 < s.length()) {
                Formatting f = codeToFormatting(Character.toLowerCase(s.charAt(i + 1)));
                if (f != null) {
                    append(out, buf, color, formats);
                    buf.setLength(0);
                    if (f == Formatting.RESET) {
                        color = null;
                        formats.clear();
                    } else if (f.isColor()) {
                        color = f;
                        formats.clear();
                    } else {
                        formats.add(f);
                    }
                    i++;
                    continue;
                }
            }
            buf.append(ch);
        }
        append(out, buf, color, formats);
        return out;
    }

    private static void append(MutableText out, StringBuilder buf, Formatting color, EnumSet<Formatting> formats) {
        if (buf.length() == 0) return;
        MutableText part = Text.literal(buf.toString());
        if (color != null) part = part.formatted(color);
        if (!formats.isEmpty()) {
            for (Formatting f : formats) {
                part = part.formatted(f);
            }
        }
        out.append(part);
    }

    private static Formatting codeToFormatting(char c) {
        return switch (c) {
            case '0' -> Formatting.BLACK;
            case '1' -> Formatting.DARK_BLUE;
            case '2' -> Formatting.DARK_GREEN;
            case '3' -> Formatting.DARK_AQUA;
            case '4' -> Formatting.DARK_RED;
            case '5' -> Formatting.DARK_PURPLE;
            case '6' -> Formatting.GOLD;
            case '7' -> Formatting.GRAY;
            case '8' -> Formatting.DARK_GRAY;
            case '9' -> Formatting.BLUE;
            case 'a' -> Formatting.GREEN;
            case 'b' -> Formatting.AQUA;
            case 'c' -> Formatting.RED;
            case 'd' -> Formatting.LIGHT_PURPLE;
            case 'e' -> Formatting.YELLOW;
            case 'f' -> Formatting.WHITE;
            case 'k' -> Formatting.OBFUSCATED;
            case 'l' -> Formatting.BOLD;
            case 'm' -> Formatting.STRIKETHROUGH;
            case 'n' -> Formatting.UNDERLINE;
            case 'o' -> Formatting.ITALIC;
            case 'r' -> Formatting.RESET;
            default -> null;
        };
    }
}
