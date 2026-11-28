package dev.kairo.ui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

/** Primitivas de desenho (cantos arredondados, texto escalado) sobre o DrawContext. */
public final class Gfx {
    private Gfx() {}

    public static int alpha(int argb, float mul) {
        int a = Math.round(((argb >>> 24) & 0xFF) * Math.max(0f, Math.min(1f, mul)));
        return (a << 24) | (argb & 0xFFFFFF);
    }

    public static int mix(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int ar = (a >> 16) & 255, ag = (a >> 8) & 255, ab = a & 255, aa = (a >>> 24) & 255;
        int br = (b >> 16) & 255, bg = (b >> 8) & 255, bb = b & 255, ba = (b >>> 24) & 255;
        return (Math.round(aa + (ba - aa) * t) << 24) | (Math.round(ar + (br - ar) * t) << 16)
            | (Math.round(ag + (bg - ag) * t) << 8) | Math.round(ab + (bb - ab) * t);
    }

    public static boolean in(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static int inset(int r, int i) {
        double dy = r - i - 0.5;
        return (int) Math.round(r - Math.sqrt(Math.max(0, r * r - dy * dy)));
    }

    public static void roundRect(DrawContext c, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0 || (color >>> 24) == 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) {
            c.fill(x, y, x + w, y + h, color);
            return;
        }
        for (int i = 0; i < r; i++) {
            int in = inset(r, i);
            c.fill(x + in, y + i, x + w - in, y + i + 1, color);
            c.fill(x + in, y + h - 1 - i, x + w - in, y + h - i, color);
        }
        c.fill(x, y + r, x + w, y + h - r, color);
    }

    /** Contorno de 1px acompanhando os cantos arredondados. */
    public static void roundOutline(DrawContext c, int x, int y, int w, int h, int r, int color) {
        if (w <= 1 || h <= 1 || (color >>> 24) == 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) {
            c.fill(x, y, x + w, y + 1, color);
            c.fill(x, y + h - 1, x + w, y + h, color);
            c.fill(x, y + 1, x + 1, y + h - 1, color);
            c.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
            return;
        }
        int prev = inset(r, 0);
        c.fill(x + prev, y, x + w - prev, y + 1, color);
        c.fill(x + prev, y + h - 1, x + w - prev, y + h, color);
        for (int i = 1; i < r; i++) {
            int in = inset(r, i);
            int end = Math.max(prev, in + 1);
            c.fill(x + in, y + i, x + end, y + i + 1, color);
            c.fill(x + w - end, y + i, x + w - in, y + i + 1, color);
            c.fill(x + in, y + h - 1 - i, x + end, y + h - i, color);
            c.fill(x + w - end, y + h - 1 - i, x + w - in, y + h - i, color);
            prev = in;
        }
        c.fill(x, y + r, x + 1, y + h - r, color);
        c.fill(x + w - 1, y + r, x + w, y + h - r, color);
    }

    public static void text(DrawContext c, TextRenderer tr, String s, int x, int y, int color) {
        c.drawText(tr, s, x, y, color, false);
    }

    public static void textScaled(DrawContext c, TextRenderer tr, String s, float x, float y, float scale, int color, boolean shadow) {
        MatrixStack m = c.getMatrices();
        m.push();
        m.translate(x, y, 0);
        m.scale(scale, scale, 1f);
        c.drawText(tr, s, 0, 0, color, shadow);
        m.pop();
    }

    public static int width(TextRenderer tr, String s, float scale) {
        return Math.round(tr.getWidth(s) * scale);
    }

    public static void centered(DrawContext c, TextRenderer tr, String s, int cx, int y, float scale, int color) {
        textScaled(c, tr, s, cx - tr.getWidth(s) * scale / 2f, y, scale, color, false);
    }

    public static String ellipsize(TextRenderer tr, String s, int maxW) {
        if (tr.getWidth(s) <= maxW) return s;
        String dots = "...";
        int dw = tr.getWidth(dots);
        int n = s.length();
        while (n > 0 && tr.getWidth(s.substring(0, n)) + dw > maxW) n--;
        return s.substring(0, n) + dots;
    }

    /** Cartao padrao do Kairo: fundo, borda sutil. */
    public static void card(DrawContext c, int x, int y, int w, int h, int fill, int border) {
        roundRect(c, x, y, w, h, 4, fill);
        roundOutline(c, x, y, w, h, 4, border);
    }
}
