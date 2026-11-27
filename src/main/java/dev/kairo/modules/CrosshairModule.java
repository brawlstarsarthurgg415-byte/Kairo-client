package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.KModule;
import dev.kairo.core.Setting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;

/** Mira personalizada. Substitui a mira padrao enquanto estiver ativa. */
public final class CrosshairModule extends KModule {
    private final Setting style = add(Setting.choice("style", "Estilo", 0, "Cruz", "Ponto", "Círculo", "Cruz + ponto"));
    private final Setting length = add(Setting.integer("length", "Comprimento", 6, 1, 30, 1));
    private final Setting thickness = add(Setting.integer("thickness", "Espessura", 2, 1, 6, 1));
    private final Setting gap = add(Setting.integer("gap", "Espaçamento", 3, 0, 20, 1));
    private final Setting color = add(Setting.color("color", "Cor", 0x55FF55));
    private final Setting alpha = add(Setting.integer("alpha", "Opacidade (%)", 100, 10, 100, 10));
    private final Setting outline = add(Setting.bool("outline", "Contorno preto", true));

    public CrosshairModule() {
        super("crosshair", "Mira personalizada", "Mira configurável: cruz, ponto, círculo, cor, tamanho e contorno.",
            Category.PVP, false);
    }

    public void draw(DrawContext ctx) {
        int cx = ctx.getScaledWindowWidth() / 2;
        int cy = ctx.getScaledWindowHeight() / 2;
        int t = thickness.get();
        int len = length.get();
        int g = gap.get();
        int hx = cx - t / 2;
        int hy = cy - t / 2;
        List<int[]> r = new ArrayList<>();
        int st = style.get();
        if (st == 0 || st == 3) {
            r.add(new int[] {cx - g - len, hy, cx - g, hy + t});
            r.add(new int[] {cx + g + (t % 2 == 0 ? 0 : 1) , hy, cx + g + len + (t % 2 == 0 ? 0 : 1), hy + t});
            r.add(new int[] {hx, cy - g - len, hx + t, cy - g});
            r.add(new int[] {hx, cy + g + (t % 2 == 0 ? 0 : 1), hx + t, cy + g + len + (t % 2 == 0 ? 0 : 1)});
        }
        if (st == 1 || st == 3) {
            r.add(new int[] {hx, hy, hx + t, hy + t});
        }
        if (st == 2) {
            int radius = len + g;
            int steps = Math.max(32, radius * 8);
            for (int i = 0; i < steps; i++) {
                double a = 2 * Math.PI * i / steps;
                int px = (int) Math.round(cx + radius * Math.cos(a)) - t / 2;
                int py = (int) Math.round(cy + radius * Math.sin(a)) - t / 2;
                r.add(new int[] {px, py, px + t, py + t});
            }
        }
        int a = alpha.get() * 255 / 100;
        if (outline.bool()) {
            int oc = (a << 24);
            for (int[] q : r) ctx.fill(q[0] - 1, q[1] - 1, q[2] + 1, q[3] + 1, oc);
        }
        int c = (a << 24) | color.get();
        for (int[] q : r) ctx.fill(q[0], q[1], q[2], q[3], c);
    }
}
