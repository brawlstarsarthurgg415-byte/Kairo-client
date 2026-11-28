package dev.kairo.ui;

import dev.kairo.core.KModule;
import dev.kairo.modules.CrosshairModule;
import dev.kairo.modules.HudModule;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/** Grade de cartoes de modulo. INFO = nome/descricao/interruptor; PREVIEW = previa + Adicionar/Remover. */
public final class ModuleGridList extends ScrollList {
    public enum Style { INFO, PREVIEW }

    private static final int GAP = 6;
    private final List<KModule> modules;
    private final Style style;
    private final Consumer<KModule> onOpen;
    private final Runnable onChanged;

    public ModuleGridList(List<KModule> modules, Style style, Consumer<KModule> onOpen, Runnable onChanged) {
        this.modules = modules;
        this.style = style;
        this.onOpen = onOpen;
        this.onChanged = onChanged;
    }

    private int cols() {
        int min = style == Style.INFO ? 150 : 120;
        return Math.max(1, (w + GAP) / (min + GAP));
    }

    private int cardW() { return (w - GAP * (cols() - 1) - 4) / cols(); }

    private int cardH() { return style == Style.INFO ? 44 : 92; }

    @Override
    protected int rowCount() { return (modules.size() + cols() - 1) / cols(); }

    @Override
    protected int rowHeight(int row) { return cardH() + GAP; }

    @Override
    protected void renderRow(DrawContext c, TextRenderer tr, int row, int rx, int ry, int rw, int mx, int my) {
        int cols = cols(), cw = cardW(), ch = cardH();
        for (int i = 0; i < cols; i++) {
            int idx = row * cols + i;
            if (idx >= modules.size()) break;
            int cx = rx + i * (cw + GAP);
            card(c, tr, modules.get(idx), cx, ry, cw, ch, Gfx.in(mx, my, cx, ry, cw, ch));
        }
    }

    private void card(DrawContext c, TextRenderer tr, KModule m, int x, int y, int w, int h, boolean hover) {
        boolean on = m.isEnabled();
        Gfx.card(c, x, y, w, h, hover ? Theme.SURFACE3 : Theme.SURFACE2, on ? Theme.accentA(0.55f) : Theme.BORDER);
        if (style == Style.INFO) {
            if (on) c.fill(x + 1, y + 6, x + 3, y + h - 6, Theme.accent());
            Gfx.text(c, tr, Gfx.ellipsize(tr, m.name, w - 46), x + 9, y + 8, Theme.TEXT);
            Gfx.text(c, tr, Gfx.ellipsize(tr, m.description, w - 16), x + 9, y + 24, Theme.MUTED);
            int tx = x + w - 30, ty = y + 7;
            Gfx.roundRect(c, tx, ty, 22, 11, 5, on ? Theme.accent() : Theme.SURFACE3);
            Gfx.roundRect(c, on ? tx + 12 : tx + 1, ty + 1, 9, 9, 4, 0xFFFFFFFF);
        } else {
            int bw = w - 8, bh = 50;
            int px = x + 4, py = y + 4;
            Gfx.roundRect(c, px, py, bw, bh, 3, 0xFF0D1017);
            c.enableScissor(px, py, px + bw, py + bh);
            MinecraftClient mc = MinecraftClient.getInstance();
            if (m instanceof HudModule hm) {
                int w0 = hm.width(mc, HudModule.Mode.DEMO), h0 = hm.height(mc, HudModule.Mode.DEMO);
                if (w0 > 0 && h0 > 0) {
                    float s = Math.min(1f, Math.min((bw - 8) / (float) w0, (bh - 8) / (float) h0));
                    hm.renderAt(c, mc, px + (bw - w0 * s) / 2f, py + (bh - h0 * s) / 2f, s, HudModule.Mode.DEMO);
                }
            } else if (m instanceof CrosshairModule cm) {
                cm.drawAt(c, px + bw / 2, py + bh / 2);
            }
            c.disableScissor();
            Gfx.text(c, tr, Gfx.ellipsize(tr, m.name, w - 10), x + 6, y + 58, Theme.TEXT);
            Btn.drawStatic(c, tr, on ? "Remover" : "Adicionar", x + 4, y + h - 20, w - 8, 16, !on);
        }
    }

    @Override
    protected void onTap(int row, int lx, int ly) {
        int cols = cols(), cw = cardW();
        int col = Math.min(cols - 1, lx / (cw + GAP));
        int idx = row * cols + col;
        if (idx >= modules.size()) return;
        int inCardX = lx - col * (cw + GAP);
        if (inCardX > cw) return;
        KModule m = modules.get(idx);
        if (style == Style.INFO) {
            boolean onSwitch = inCardX >= cw - 36 && ly < 22;
            if (onSwitch && !m.isCore()) {
                m.toggle();
                dev.kairo.core.ModuleManager.markDirty();
                onChanged.run();
            } else {
                onOpen.accept(m);
            }
        } else if (ly >= cardH() - 22) {
            m.toggle();
            dev.kairo.core.ModuleManager.markDirty();
            onChanged.run();
        } else if (m instanceof HudModule) {
            onOpen.accept(m);
        }
    }
}
