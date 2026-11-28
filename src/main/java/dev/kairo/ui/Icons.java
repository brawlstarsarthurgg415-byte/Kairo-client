package dev.kairo.ui;

import net.minecraft.client.gui.DrawContext;

/** Icones simples desenhados com retangulos (sem texturas). Area de 12x12 escalada por 's'. */
public final class Icons {
    public static final int HOME = 0, PERSON = 1, GLOBE = 2, GEAR = 3, GRID = 4, POWER = 5, SWORD = 6,
        LAYOUT = 7, EYE = 8, BOLT = 9, TOOLS = 10, PROFILE = 11, PLUS = 12, BACK = 13, SEARCH = 14;

    private Icons() {}

    private static void r(DrawContext c, int x, int y, int s, int a, int b, int w, int h, int col) {
        c.fill(x + a * s, y + b * s, x + (a + w) * s, y + (b + h) * s, col);
    }

    public static void draw(DrawContext c, int id, int x, int y, int s, int col) {
        switch (id) {
            case HOME -> {
                r(c, x, y, s, 5, 0, 2, 1, col); r(c, x, y, s, 3, 1, 6, 1, col); r(c, x, y, s, 1, 2, 10, 1, col);
                r(c, x, y, s, 2, 4, 8, 7, col); r(c, x, y, s, 5, 7, 2, 4, Theme.BG);
            }
            case PERSON -> {
                r(c, x, y, s, 4, 0, 4, 4, col); r(c, x, y, s, 2, 6, 8, 5, col); r(c, x, y, s, 3, 5, 6, 1, col);
            }
            case GLOBE -> {
                r(c, x, y, s, 3, 0, 6, 12, col); r(c, x, y, s, 1, 2, 10, 8, col); r(c, x, y, s, 0, 4, 12, 4, col);
                r(c, x, y, s, 5, 1, 2, 10, Theme.BG); r(c, x, y, s, 1, 5, 10, 2, Theme.BG);
            }
            case GEAR -> {
                r(c, x, y, s, 4, 0, 4, 12, col); r(c, x, y, s, 0, 4, 12, 4, col); r(c, x, y, s, 2, 2, 8, 8, col);
                r(c, x, y, s, 4, 4, 4, 4, Theme.BG);
            }
            case GRID -> {
                r(c, x, y, s, 0, 0, 5, 5, col); r(c, x, y, s, 7, 0, 5, 5, col);
                r(c, x, y, s, 0, 7, 5, 5, col); r(c, x, y, s, 7, 7, 5, 5, col);
            }
            case POWER -> {
                r(c, x, y, s, 5, 0, 2, 6, col); r(c, x, y, s, 2, 3, 2, 7, col); r(c, x, y, s, 8, 3, 2, 7, col);
                r(c, x, y, s, 3, 9, 6, 2, col);
            }
            case SWORD -> {
                r(c, x, y, s, 8, 0, 3, 3, col); r(c, x, y, s, 6, 2, 3, 3, col); r(c, x, y, s, 4, 4, 3, 3, col);
                r(c, x, y, s, 1, 6, 6, 2, col); r(c, x, y, s, 2, 8, 2, 3, col);
            }
            case LAYOUT -> {
                r(c, x, y, s, 0, 0, 12, 3, col); r(c, x, y, s, 0, 4, 4, 8, col); r(c, x, y, s, 5, 4, 7, 8, col);
                r(c, x, y, s, 6, 5, 5, 6, Theme.BG);
            }
            case EYE -> {
                r(c, x, y, s, 3, 2, 6, 1, col); r(c, x, y, s, 1, 3, 10, 6, col); r(c, x, y, s, 3, 9, 6, 1, col);
                r(c, x, y, s, 4, 4, 4, 4, Theme.BG); r(c, x, y, s, 5, 5, 2, 2, col);
            }
            case BOLT -> {
                r(c, x, y, s, 6, 0, 4, 3, col); r(c, x, y, s, 4, 3, 4, 3, col); r(c, x, y, s, 2, 6, 6, 2, col);
                r(c, x, y, s, 4, 8, 3, 4, col);
            }
            case TOOLS -> {
                r(c, x, y, s, 1, 1, 4, 3, col); r(c, x, y, s, 3, 4, 2, 7, col); r(c, x, y, s, 7, 0, 3, 7, col);
                r(c, x, y, s, 6, 7, 5, 3, col);
            }
            case PROFILE -> {
                r(c, x, y, s, 0, 1, 12, 10, col); r(c, x, y, s, 1, 2, 10, 8, Theme.BG);
                r(c, x, y, s, 3, 3, 3, 3, col); r(c, x, y, s, 2, 7, 5, 2, col); r(c, x, y, s, 8, 4, 2, 1, col);
                r(c, x, y, s, 8, 6, 2, 1, col);
            }
            case PLUS -> {
                r(c, x, y, s, 5, 1, 2, 10, col); r(c, x, y, s, 1, 5, 10, 2, col);
            }
            case BACK -> {
                r(c, x, y, s, 2, 5, 9, 2, col); r(c, x, y, s, 2, 4, 2, 4, col); r(c, x, y, s, 4, 3, 1, 6, col);
                r(c, x, y, s, 5, 2, 1, 8, col);
            }
            case SEARCH -> {
                r(c, x, y, s, 2, 0, 5, 1, col); r(c, x, y, s, 1, 1, 7, 5, col); r(c, x, y, s, 2, 6, 5, 1, col);
                r(c, x, y, s, 3, 2, 3, 3, Theme.BG); r(c, x, y, s, 7, 7, 2, 2, col); r(c, x, y, s, 9, 9, 2, 2, col);
            }
            default -> { }
        }
    }
}
