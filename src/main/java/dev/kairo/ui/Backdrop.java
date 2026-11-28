package dev.kairo.ui;

import net.minecraft.client.gui.DrawContext;

/** Fundo escuro do Kairo: gradiente, brilho de destaque e quadradinhos flutuando (barato). */
public final class Backdrop {
    private Backdrop() {}

    public static void draw(DrawContext c, int w, int h) {
        c.fillGradient(0, 0, w, h, 0xFF0B0E14, 0xFF06070B);
        boolean anim = Theme.animations();
        float t = anim ? (System.currentTimeMillis() % 600000L) / 1000f : 0f;
        // brilho suave de destaque (camadas translucidas concentricas)
        int gx = (int) (w * 0.78f + (anim ? Math.sin(t * 0.25f) * w * 0.04f : 0));
        int gy = (int) (h * 0.22f + (anim ? Math.cos(t * 0.2f) * h * 0.05f : 0));
        for (int i = 6; i >= 1; i--) {
            int rr = i * Math.max(14, h / 9);
            Gfx.roundRect(c, gx - rr, gy - rr, rr * 2, rr * 2, rr, Theme.accentA(0.012f));
        }
        // quadradinhos
        int n = 16;
        for (int i = 0; i < n; i++) {
            float speed = 6f + (i % 5) * 2f;
            float y = h - ((t * speed + i * 53f) % (h + 24f));
            int x = (int) ((i * 97f + 13f * i * i) % Math.max(1, w));
            int s = 2 + (i % 3);
            float a = 0.05f + 0.03f * (i % 4);
            c.fill(x, (int) y, x + s, (int) y + s, Theme.accentA(a));
        }
    }
}
