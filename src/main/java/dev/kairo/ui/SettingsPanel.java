package dev.kairo.ui;

import dev.kairo.core.KModule;
import dev.kairo.core.Setting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/** Gera controles (interruptor, slider, opcoes, cor) para as configuracoes de um modulo. */
public final class SettingsPanel extends ScrollList {
    private final List<Setting> settings = new ArrayList<>();
    private Runnable onChange = () -> {};

    public SettingsPanel(KModule module) {
        for (Setting s : module.settings()) if (!s.isHidden()) settings.add(s);
    }

    public SettingsPanel onChange(Runnable r) { onChange = r; return this; }

    @Override
    protected int rowCount() { return settings.size(); }

    @Override
    protected int rowHeight(int row) {
        return switch (settings.get(row).kind) {
            case BOOL -> 24;
            case INT -> 30;
            case CHOICE -> 18 + chipLines(settings.get(row)) * 17;
            case COLOR -> 72;
        };
    }

    @Override
    protected boolean isSlider(int row) {
        Setting.Kind k = settings.get(row).kind;
        return k == Setting.Kind.INT || k == Setting.Kind.COLOR;
    }

    private int chipLines(Setting s) {
        var tr = net.minecraft.client.MinecraftClient.getInstance().textRenderer;
        int cx = PAD, lines = 1;
        for (String ch : s.choices) {
            int cw = tr.getWidth(ch) + 10;
            if (cx + cw > w - PAD && cx > PAD) { cx = PAD; lines++; }
            cx += cw + 3;
        }
        return lines;
    }

    private static final int PAD = 6;
    private static final int SW = 14; // largura de cada amostra de cor

    @Override
    protected void renderRow(DrawContext c, TextRenderer tr, int row, int rx, int ry, int rw, int mx, int my) {
        Setting s = settings.get(row);
        int accent = Theme.accent();
        int right = rx + rw - PAD - 4;
        Gfx.text(c, tr, s.label, rx + PAD, ry + 4, Theme.TEXT);
        switch (s.kind) {
            case BOOL -> {
                int tw = 22, th = 11;
                int tx = right - tw, ty = ry + 3;
                Gfx.roundRect(c, tx, ty, tw, th, 5, s.bool() ? accent : Theme.SURFACE3);
                int kx = s.bool() ? tx + tw - th + 1 : tx + 1;
                Gfx.roundRect(c, kx, ty + 1, th - 2, th - 2, 4, 0xFFFFFFFF);
            }
            case INT -> {
                String v = String.valueOf(s.get());
                Gfx.text(c, tr, v, right - tr.getWidth(v), ry + 4, Theme.MUTED);
                int tx = rx + PAD, tw = rw - PAD * 2 - 4, ty = ry + 19;
                c.fill(tx, ty, tx + tw, ty + 3, Theme.SURFACE3);
                float f = (s.get() - s.min) / (float) Math.max(1, s.max - s.min);
                c.fill(tx, ty, tx + Math.round(tw * f), ty + 3, accent);
                int kx = tx + Math.round(tw * f);
                Gfx.roundRect(c, kx - 4, ty - 3, 8, 9, 3, 0xFFFFFFFF);
            }
            case CHOICE -> {
                int cx = PAD, line = 0;
                for (int i = 0; i < s.choices.length; i++) {
                    int cw = tr.getWidth(s.choices[i]) + 10;
                    if (cx + cw > w - PAD && cx > PAD) { cx = PAD; line++; }
                    boolean on = i == s.get();
                    int bx = rx + cx, by = ry + 16 + line * 17;
                    Gfx.roundRect(c, bx, by, cw, 14, 3, on ? Theme.accentA(0.28f) : Theme.SURFACE3);
                    if (on) Gfx.roundOutline(c, bx, by, cw, 14, 3, accent);
                    Gfx.text(c, tr, s.choices[i], bx + 5, by + 3, on ? Theme.TEXT : Theme.MUTED);
                    cx += cw + 3;
                }
            }
            case COLOR -> {
                String hex = String.format("#%06X", s.get());
                Gfx.text(c, tr, hex, right - tr.getWidth(hex), ry + 4, Theme.MUTED);
                int perRow = Math.max(1, (rw - PAD * 2) / (SW + 2));
                for (int i = 0; i < Setting.PALETTE.length; i++) {
                    int sx = rx + PAD + (i % perRow) * (SW + 2);
                    int sy = ry + 16 + (i / perRow) * 12;
                    Gfx.roundRect(c, sx, sy, SW, 10, 2, 0xFF000000 | Setting.PALETTE[i]);
                    if (Setting.PALETTE[i] == s.get()) Gfx.roundOutline(c, sx - 1, sy - 1, SW + 2, 12, 2, accent);
                }
                int rowsUsed = (Setting.PALETTE.length + perRow - 1) / perRow;
                int hy = ry + 16 + rowsUsed * 12 + 2;
                int tx = rx + PAD, tw = rw - PAD * 2 - 4;
                int segs = 24;
                for (int i = 0; i < segs; i++) {
                    int x1 = tx + tw * i / segs, x2 = tx + tw * (i + 1) / segs;
                    c.fill(x1, hy, x2, hy + 6, 0xFF000000 | hsv(i / (float) segs, 1f, 1f));
                }
                float[] hv = rgbToHsv(s.get());
                Gfx.roundRect(c, tx + Math.round(tw * hv[0]) - 2, hy - 2, 4, 10, 2, 0xFFFFFFFF);
            }
        }
        c.fill(rx + PAD, ry + rowHeight(row) - 1, rx + rw - PAD, ry + rowHeight(row), Theme.BORDER);
    }

    @Override
    protected void onTap(int row, int lx, int ly) {
        Setting s = settings.get(row);
        switch (s.kind) {
            case BOOL -> { s.cycle(true); onChange.run(); }
            case CHOICE -> {
                var tr = net.minecraft.client.MinecraftClient.getInstance().textRenderer;
                int cx = PAD, line = 0;
                for (int i = 0; i < s.choices.length; i++) {
                    int cw = tr.getWidth(s.choices[i]) + 10;
                    if (cx + cw > w - PAD && cx > PAD) { cx = PAD; line++; }
                    int by = 16 + line * 17;
                    if (lx >= cx && lx < cx + cw + 3 && ly >= by && ly < by + 17) { s.set(i); onChange.run(); return; }
                    cx += cw + 3;
                }
            }
            case COLOR -> {
                int perRow = Math.max(1, (w - PAD * 2) / (SW + 2));
                for (int i = 0; i < Setting.PALETTE.length; i++) {
                    int sx = PAD + (i % perRow) * (SW + 2);
                    int sy = 16 + (i / perRow) * 12;
                    if (lx >= sx && lx < sx + SW + 2 && ly >= sy && ly < sy + 12) {
                        s.set(Setting.PALETTE[i]);
                        onChange.run();
                        return;
                    }
                }
            }
            default -> { }
        }
    }

    @Override
    protected void onSlide(int row, int lx, int ly) {
        Setting s = settings.get(row);
        int tw = w - PAD * 2 - 4;
        float f = Math.max(0f, Math.min(1f, (lx - PAD) / (float) tw));
        if (s.kind == Setting.Kind.INT) {
            int v = s.min + Math.round(f * (s.max - s.min) / s.step) * s.step;
            s.set(v);
            onChange.run();
        } else if (s.kind == Setting.Kind.COLOR) {
            int perRow = Math.max(1, (w - PAD * 2) / (SW + 2));
            int rowsUsed = (Setting.PALETTE.length + perRow - 1) / perRow;
            int hueTop = 16 + rowsUsed * 12;
            if (ly >= hueTop - 2) {
                s.set(hsv(f, 1f, 1f));
                onChange.run();
            }
        }
    }

    // ---------- cor ----------
    static int hsv(float h, float s, float v) {
        h = (h - (float) Math.floor(h)) * 6f;
        int i = (int) h;
        float f = h - i, p = v * (1 - s), q = v * (1 - s * f), t = v * (1 - s * (1 - f));
        float r, g, b;
        switch (i) {
            case 0 -> { r = v; g = t; b = p; }
            case 1 -> { r = q; g = v; b = p; }
            case 2 -> { r = p; g = v; b = t; }
            case 3 -> { r = p; g = q; b = v; }
            case 4 -> { r = t; g = p; b = v; }
            default -> { r = v; g = p; b = q; }
        }
        return (Math.round(r * 255) << 16) | (Math.round(g * 255) << 8) | Math.round(b * 255);
    }

    static float[] rgbToHsv(int rgb) {
        float r = ((rgb >> 16) & 255) / 255f, g = ((rgb >> 8) & 255) / 255f, b = (rgb & 255) / 255f;
        float max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b)), d = max - min;
        float h;
        if (d == 0) h = 0;
        else if (max == r) h = ((g - b) / d % 6f) / 6f;
        else if (max == g) h = ((b - r) / d + 2f) / 6f;
        else h = ((r - g) / d + 4f) / 6f;
        if (h < 0) h += 1f;
        return new float[] {h, max == 0 ? 0 : d / max, max};
    }
}
