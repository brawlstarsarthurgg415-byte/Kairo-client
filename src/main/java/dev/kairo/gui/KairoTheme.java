package dev.kairo.gui;

import dev.kairo.core.AppearanceManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/** Paleta e recursos visuais compartilhados pelas telas do Kairo. */
public final class KairoTheme {
    public static final int BG = 0xF0101420;
    public static final int PANEL = 0xE91B2231;
    public static final int CARD = 0xC0161D2B;
    public static final int CARD_ACTIVE = 0xE01E2739;
    public static final int MUTED = 0xFF9AA8BC;
    public static final int TEXT = 0xFFFFFFFF;
    public static final int DANGER = 0xFFFF6E6E;
    public static final int SUCCESS = 0xFF7CFF6B;

    /** Sprites com nine_slice definido em assets/kairo/textures/gui/sprites/*.json */
    public static final Identifier BUTTON_TEXTURE = Identifier.of("kairo", "button");
    public static final Identifier PANEL_TEXTURE = Identifier.of("kairo", "panel");

    private KairoTheme() {}

    /** Cor de destaque configuravel (0xAARRGGBB). */
    public static int accent() { return AppearanceManager.accent(); }

    public static void panel(DrawContext ctx, int x, int y, int w, int h) {
        ctx.drawGuiTexture(PANEL_TEXTURE, x, y, w, h);
    }

    public static void button(DrawContext ctx, int x, int y, int w, int h, boolean hovered, boolean activeState) {
        ctx.drawGuiTexture(BUTTON_TEXTURE, x, y, w, h);
        if (activeState) {
            // Barra de destaque na base do botao.
            ctx.fill(x + 3, y + h - 2, x + w - 3, y + h - 1, hovered ? accent() : 0xFF3A4358);
        }
    }

    public static void border(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + 1, color);
        ctx.fill(x, y + h - 1, x + w, y + h, color);
        ctx.fill(x, y, x + 1, y + h, color);
        ctx.fill(x + w - 1, y, x + w, y + h, color);
    }
}
