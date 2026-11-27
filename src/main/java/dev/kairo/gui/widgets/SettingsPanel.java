package dev.kairo.gui.widgets;

import dev.kairo.core.KModule;
import dev.kairo.core.Setting;
import dev.kairo.gui.KairoTheme;
import java.util.List;
import net.minecraft.client.gui.DrawContext;

/**
 * Painel rolavel com as configuracoes de um modulo, em linhas de toque amplas:
 * booleano alterna com toque, inteiro tem -/+, escolha tem ◀/▶ e cor mostra amostra.
 */
public class SettingsPanel extends KScrollPanel {
    private static final int ROW_H = 24;
    private static final int ZONE = 18;

    private final Runnable onChange;
    private KModule module;

    public SettingsPanel(int x, int y, int w, int h, Runnable onChange) {
        super(x, y, w, h);
        this.onChange = onChange;
    }

    public void setModule(KModule module) {
        this.module = module;
        scrollTo(0);
    }

    @Override
    protected int contentHeight() {
        if (module == null) return 0;
        return module.settings().size() * ROW_H + 4;
    }

    private List<Setting> settings() {
        return module == null ? List.of() : module.settings();
    }

    @Override
    protected void drawRows(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int x = getX() + 3;
        int w = getWidth() - 8;
        int y = getY() + 2;
        for (Setting s : settings()) {
            ctx.fill(x, y, x + w, y + ROW_H - 2, KairoTheme.CARD);
            String label = fit(s.label, w - 90);
            ctx.drawText(textRenderer, label, x + 5, y + 8, KairoTheme.TEXT, false);
            int cy = y + (ROW_H - 2 - 8) / 2;
            switch (s.kind) {
                case BOOL -> {
                    String v = s.bool() ? "Ligado" : "Desligado";
                    int vc = s.bool() ? KairoTheme.SUCCESS : KairoTheme.MUTED;
                    ctx.drawText(textRenderer, v, x + w - 5 - textRenderer.getWidth(v), cy, vc, false);
                }
                case INT -> {
                    drawZone(ctx, x + w - ZONE * 2 - 30, y + 2, "+");
                    drawZone(ctx, x + w - ZONE, y + 2, "−");
                    ctx.drawCenteredTextWithShadow(textRenderer, net.minecraft.text.Text.literal(s.display()),
                        x + w - ZONE - 15, cy, KairoTheme.accent());
                }
                case CHOICE -> {
                    drawZone(ctx, x + w - ZONE * 2 - 40, y + 2, "▶");
                    drawZone(ctx, x + w - ZONE, y + 2, "◀");
                    ctx.drawCenteredTextWithShadow(textRenderer, net.minecraft.text.Text.literal(fit(s.display(), 34)),
                        x + w - ZONE - 20, cy, KairoTheme.accent());
                }
                case COLOR -> {
                    drawZone(ctx, x + w - ZONE * 2 - 26, y + 2, "▶");
                    drawZone(ctx, x + w - ZONE, y + 2, "◀");
                    ctx.fill(x + w - ZONE - 22, y + 5, x + w - ZONE - 8, y + ROW_H - 7, 0xFF000000 | s.get());
                }
            }
            y += ROW_H;
        }
    }

    private void drawZone(DrawContext ctx, int zx, int zy, String glyph) {
        ctx.fill(zx, zy, zx + ZONE, zy + ROW_H - 6, KairoTheme.CARD_ACTIVE);
        ctx.drawCenteredTextWithShadow(textRenderer, net.minecraft.text.Text.literal(glyph),
            zx + ZONE / 2, zy + 5, KairoTheme.MUTED);
    }

    private String fit(String s, int widthPx) {
        if (textRenderer.getWidth(s) <= widthPx) return s;
        while (!s.isEmpty() && textRenderer.getWidth(s + "…") > widthPx) s = s.substring(0, s.length() - 1);
        return s.isEmpty() ? s : s + "…";
    }

    @Override
    protected boolean rowClicked(double mouseX, double localY, int button) {
        int idx = (int) (localY / ROW_H);
        List<Setting> ss = settings();
        if (idx < 0 || idx >= ss.size()) return false;
        Setting s = ss.get(idx);
        int lx = (int) mouseX - getX();
        int w = getWidth() - 8;
        boolean forward = button != 1; // clique direito/long = voltar
        switch (s.kind) {
            case BOOL -> s.cycle(true);
            case INT -> {
                // zonas: [w-2Z-30, w-Z-30] = "+", [w-Z, w] = "−"; meio = cicla
                if (lx >= w - 2 * ZONE - 30 && lx < w - ZONE - 30) s.cycle(true);
                else if (lx >= w - ZONE) s.cycle(false);
                else s.cycle(forward);
            }
            case CHOICE -> {
                if (lx >= w - 2 * ZONE - 40 && lx < w - ZONE - 40) s.cycle(true);
                else if (lx >= w - ZONE) s.cycle(false);
                else s.cycle(forward);
            }
            case COLOR -> {
                if (lx >= w - 2 * ZONE - 26 && lx < w - ZONE - 26) s.cycle(true);
                else if (lx >= w - ZONE) s.cycle(false);
                else s.cycle(forward);
            }
        }
        if (onChange != null) onChange.run();
        return true;
    }
}
