package dev.kairo.ui;

import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/** Botao do Kairo. A acao dispara ao SOLTAR dentro do botao (bom para toque). */
public final class Btn {
    public int x, y, w, h;
    public String label;
    public int icon = -1;
    public boolean primary;
    public boolean enabled = true;
    public boolean selected;
    private boolean pressed;
    private final Runnable action;

    public Btn(String label, Runnable action) {
        this.label = label;
        this.action = action;
    }

    public Btn icon(int i) { icon = i; return this; }

    public Btn primary() { primary = true; return this; }

    public Btn bounds(int x, int y, int w, int h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
        return this;
    }

    public void render(DrawContext c, TextRenderer tr, int mx, int my) {
        boolean hover = enabled && Gfx.in(mx, my, x, y, w, h);
        int accent = Theme.accent();
        int fill;
        int text;
        if (!enabled) { fill = Theme.SURFACE2; text = Theme.MUTED; }
        else if (primary) { fill = pressed ? Gfx.mix(accent, 0xFF000000, 0.25f) : hover ? Gfx.mix(accent, 0xFFFFFFFF, 0.12f) : accent; text = 0xFF0A0C10; }
        else if (selected) { fill = Theme.accentA(0.22f); text = Theme.TEXT; }
        else { fill = pressed ? Theme.SURFACE3 : hover ? Theme.SURFACE3 : Theme.SURFACE2; text = Theme.TEXT; }
        Gfx.roundRect(c, x, y, w, h, 4, fill);
        if (!primary) Gfx.roundOutline(c, x, y, w, h, 4, selected ? Theme.accentA(0.7f) : Theme.BORDER);
        int iconW = icon >= 0 ? 14 : 0;
        int tw = tr.getWidth(label);
        int cx = x + (w - tw - iconW) / 2;
        if (icon >= 0) {
            Icons.draw(c, icon, cx, y + (h - 12) / 2, 1, text);
            cx += iconW;
        }
        if (!label.isEmpty()) c.drawText(tr, label, cx, y + (h - 8) / 2, text, false);
    }

    public boolean down(double mx, double my) {
        pressed = enabled && Gfx.in(mx, my, x, y, w, h);
        return pressed;
    }

    public boolean up(double mx, double my) {
        boolean fire = pressed && enabled && Gfx.in(mx, my, x, y, w, h);
        pressed = false;
        if (fire) action.run();
        return fire;
    }

    /** Desenha um botao sem estado (usado dentro de cartoes de lista). */
    public static void drawStatic(DrawContext c, TextRenderer tr, String label, int x, int y, int w, int h, boolean primary) {
        int accent = Theme.accent();
        Gfx.roundRect(c, x, y, w, h, 3, primary ? accent : Theme.SURFACE3);
        if (!primary) Gfx.roundOutline(c, x, y, w, h, 3, Theme.BORDER);
        c.drawText(tr, label, x + (w - tr.getWidth(label)) / 2, y + (h - 8) / 2, primary ? 0xFF0A0C10 : Theme.TEXT, false);
    }

    public static boolean downAll(List<Btn> list, double mx, double my) {
        boolean any = false;
        for (Btn b : list) if (b.down(mx, my)) any = true;
        return any;
    }

    /** Dispara no maximo um botao. Cuidado: a acao pode trocar a tela. */
    public static boolean upAll(List<Btn> list, double mx, double my) {
        Btn hit = null;
        for (Btn b : list) {
            boolean was = b.pressed;
            if (was && b.enabled && Gfx.in(mx, my, b.x, b.y, b.w, b.h)) hit = b;
            b.pressed = false;
        }
        if (hit != null) { hit.action.run(); return true; }
        return false;
    }
}
