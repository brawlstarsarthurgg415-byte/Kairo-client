package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.KModule;
import dev.kairo.core.Setting;
import net.minecraft.client.gui.DrawContext;

/**
 * Mira personalizada. Quando ativa, InGameHudMixin cancela a mira vanilla (nunca ha duas miras).
 * drawAt() e usado tanto no jogo quanto na previa.
 */
public final class CrosshairModule extends KModule {
    private final int[][] pool = new int[160][4];
    private int count;

    private final Setting style = add(Setting.choice("style", "Estilo", 0,
        "Cruz", "Ponto", "Círculo", "Cruz + ponto", "Cruz em T", "Quadrado"));
    private final Setting length = add(Setting.integer("length", "Comprimento", 6, 1, 30, 1));
    private final Setting thickness = add(Setting.integer("thickness", "Espessura", 2, 1, 6, 1));
    private final Setting gap = add(Setting.integer("gap", "Espaçamento", 3, 0, 20, 1));
    private final Setting color = add(Setting.color("color", "Cor", 0x55FF55));
    private final Setting alpha = add(Setting.integer("alpha", "Opacidade (%)", 100, 10, 100, 10));
    private final Setting outline = add(Setting.bool("outline", "Contorno preto", true));

    public CrosshairModule() {
        super("crosshair", "Mira personalizada", "Substitui a mira do jogo: estilo, cor, tamanho, espessura e contorno.",
            Category.VISUAL, false);
    }

    private void rect(int x1, int y1, int x2, int y2) {
        if (count < pool.length) {
            int[] q = pool[count++];
            q[0] = x1; q[1] = y1; q[2] = x2; q[3] = y2;
        }
    }

    public void draw(DrawContext ctx) {
        drawAt(ctx, ctx.getScaledWindowWidth() / 2, ctx.getScaledWindowHeight() / 2);
    }

    public void drawAt(DrawContext ctx, int cx, int cy) {
        int t = thickness.get();
        int len = length.get();
        int g = gap.get();
        int hx = cx - t / 2;
        int hy = cy - t / 2;
        int off = t % 2 == 0 ? 0 : 1;
        count = 0;
        int st = style.get();
        if (st == 0 || st == 3 || st == 4) {
            rect(cx - g - len, hy, cx - g, hy + t);
            rect(cx + g + off, hy, cx + g + len + off, hy + t);
            rect(hx, cy + g + off, hx + t, cy + g + len + off);
            if (st != 4) rect(hx, cy - g - len, hx + t, cy - g);
        }
        if (st == 1 || st == 3) rect(hx, hy, hx + t, hy + t);
        if (st == 2) {
            int radius = len + g;
            for (int i = 0; i < 48; i++) {
                double a = 2 * Math.PI * i / 48;
                int px = (int) Math.round(cx + radius * Math.cos(a)) - t / 2;
                int py = (int) Math.round(cy + radius * Math.sin(a)) - t / 2;
                rect(px, py, px + t, py + t);
            }
        }
        if (st == 5) {
            int half = len + g;
            rect(cx - half, cy - half, cx + half, cy - half + t);
            rect(cx - half, cy + half - t, cx + half, cy + half);
            rect(cx - half, cy - half + t, cx - half + t, cy + half - t);
            rect(cx + half - t, cy - half + t, cx + half, cy + half - t);
        }
        int a = alpha.get() * 255 / 100;
        if (outline.bool()) {
            int oc = a << 24;
            for (int i = 0; i < count; i++) {
                int[] q = pool[i];
                ctx.fill(q[0] - 1, q[1] - 1, q[2] + 1, q[3] + 1, oc);
            }
        }
        int c = (a << 24) | color.get();
        for (int i = 0; i < count; i++) {
            int[] q = pool[i];
            ctx.fill(q[0], q[1], q[2], q[3], c);
        }
    }
}
