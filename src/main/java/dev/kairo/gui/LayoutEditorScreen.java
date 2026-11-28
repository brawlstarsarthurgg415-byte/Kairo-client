package dev.kairo.gui;

import dev.kairo.core.ModuleManager;
import dev.kairo.core.ProfileManager;
import dev.kairo.modules.HudModule;
import dev.kairo.ui.Backdrop;
import dev.kairo.ui.Btn;
import dev.kairo.ui.Gfx;
import dev.kairo.ui.Icons;
import dev.kairo.ui.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/**
 * Layout Editor: toque num elemento e arraste. A previa usa o mesmo desenho do HUD real.
 * Posicoes sao guardadas como fracao do espaco livre da tela (nunca saem da tela).
 */
public class LayoutEditorScreen extends Screen {
    private static final int SNAP = 4;

    private final Screen parent;
    private HudModule selected;
    private boolean moving, scaling;
    private float grabDX, grabDY;
    private int startScale;
    private double startMX;
    private boolean guideV, guideH;
    private boolean changed;
    private final List<Btn> bar = new ArrayList<>();
    private int barH;

    public LayoutEditorScreen(Screen parent) {
        super(Text.literal("Editor de HUD"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        bar.clear();
        bar.add(new Btn("Concluir", this::close).primary());
        bar.add(new Btn("Biblioteca", () -> client.setScreen(new ElementLibraryScreen(parent))).icon(Icons.GRID));
        bar.add(new Btn("-", () -> scaleBy(-10)));
        bar.add(new Btn("+", () -> scaleBy(10)));
        bar.add(new Btn("Centro H", () -> center(true)));
        bar.add(new Btn("Centro V", () -> center(false)));
        bar.add(new Btn("Frente", () -> layer(1)));
        bar.add(new Btn("Trás", () -> layer(-1)));
        bar.add(new Btn("Restaurar", () -> {
            if (selected != null) { selected.resetPosition(); selected.setScalePct(100); changed = true; }
        }));
        bar.add(new Btn("Ocultar", () -> {
            if (selected != null) {
                selected.setEnabled(false);
                ModuleManager.markDirty();
                selected = null;
                changed = true;
            }
        }));
        // fluxo em linhas
        int x = 6, y = height - 6, rowH = 20;
        List<int[]> rows = new ArrayList<>();
        int cx = 6, line = 0;
        int[][] pos = new int[bar.size()][2];
        for (int i = 0; i < bar.size(); i++) {
            Btn b = bar.get(i);
            int bw = Math.max(26, textRenderer.getWidth(b.label) + (b.icon >= 0 ? 14 : 0) + 14);
            if (cx + bw > width - 6 && cx > 6) { cx = 6; line++; }
            pos[i][0] = cx; pos[i][1] = line;
            b.bounds(cx, 0, bw, 18);
            cx += bw + 4;
        }
        int lines = line + 1;
        barH = lines * rowH + 6;
        for (int i = 0; i < bar.size(); i++) bar.get(i).y = height - barH + 3 + pos[i][1] * rowH;
    }

    private void scaleBy(int d) {
        if (selected == null) return;
        selected.setScalePct(selected.scalePct() + d);
        changed = true;
    }

    private void layer(int d) {
        if (selected == null) return;
        selected.moveLayer(d);
        changed = true;
    }

    private void center(boolean horizontal) {
        if (selected == null) return;
        int w = selected.width(client, HudModule.Mode.DEMO), h = selected.height(client, HudModule.Mode.DEMO);
        float s = selected.scalePct() / 100f;
        int x = selected.placedX(width, w), y = selected.placedY(height, h);
        if (horizontal) x = Math.round((width - w * s) / 2f);
        else y = Math.round((height - h * s) / 2f);
        selected.placeAtPixels(x, y, width, height, w, h);
        changed = true;
    }

    private int[] rect(HudModule m) {
        int w = m.width(client, HudModule.Mode.DEMO), h = m.height(client, HudModule.Mode.DEMO);
        float s = m.scalePct() / 100f;
        return new int[] {m.placedX(width, w), m.placedY(height, h), Math.round(w * s), Math.round(h * s)};
    }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        if (client.world == null) {
            Backdrop.draw(c, width, height);
            c.fillGradient(0, height / 2, width, height, 0x00000000, 0x6638A83A);
        } else {
            c.fill(0, 0, width, height, 0x66000000);
        }
        // guias de centro
        c.fill(width / 2, 0, width / 2 + 1, height, Gfx.alpha(Theme.MUTED, 0.18f));
        c.fill(0, height / 2, width, height / 2 + 1, Gfx.alpha(Theme.MUTED, 0.18f));

        for (HudModule m : ModuleManager.hudOrder()) {
            if (!m.isEnabled()) continue;
            m.renderPlaced(c, client, HudModule.Mode.DEMO);
            int[] r = rect(m);
            if (r[2] <= 0) continue;
            boolean sel = m == selected;
            Gfx.roundOutline(c, r[0] - 2, r[1] - 2, r[2] + 4, r[3] + 4, 3, sel ? Theme.accent() : Gfx.alpha(Theme.MUTED, 0.35f));
            if (sel) c.fill(r[0] + r[2] - 3, r[1] + r[3] - 3, r[0] + r[2] + 5, r[1] + r[3] + 5, Theme.accent());
        }
        if (moving) {
            if (guideV) c.fill(width / 2, 0, width / 2 + 1, height, Theme.accent());
            if (guideH) c.fill(0, height / 2, width, height / 2 + 1, Theme.accent());
        }
        // barra superior de dica
        String tip = selected == null ? "Toque num elemento para selecionar e arraste para mover"
            : selected.name + " · escala " + selected.scalePct() + "% · arraste o canto para redimensionar";
        c.fill(0, 0, width, 14, 0xAA000000);
        Gfx.text(c, textRenderer, Gfx.ellipsize(textRenderer, tip, width - 8), 4, 3, Theme.TEXT);
        // barra de ferramentas
        c.fill(0, height - barH, width, height, 0xE6090B10);
        c.fill(0, height - barH, width, height - barH + 1, Theme.BORDER);
        boolean has = selected != null;
        for (int i = 2; i < bar.size(); i++) bar.get(i).enabled = has;
        for (Btn b : bar) b.render(c, textRenderer, mx, my);
    }

    private HudModule hit(double mx, double my) {
        List<HudModule> order = ModuleManager.hudOrder();
        for (int i = order.size() - 1; i >= 0; i--) {
            HudModule m = order.get(i);
            if (!m.isEnabled()) continue;
            int[] r = rect(m);
            if (Gfx.in(mx, my, r[0] - 3, r[1] - 3, r[2] + 6, r[3] + 6)) return m;
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (my >= height - barH) return Btn.downAll(bar, mx, my);
        if (selected != null) {
            int[] r = rect(selected);
            if (Gfx.in(mx, my, r[0] + r[2] - 8, r[1] + r[3] - 8, 20, 20)) {
                scaling = true;
                startScale = selected.scalePct();
                startMX = mx;
                return true;
            }
        }
        HudModule h = hit(mx, my);
        selected = h;
        if (h != null) {
            int[] r = rect(h);
            moving = true;
            grabDX = (float) (mx - r[0]);
            grabDY = (float) (my - r[1]);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (selected == null) return false;
        int w = selected.width(client, HudModule.Mode.DEMO), h = selected.height(client, HudModule.Mode.DEMO);
        if (scaling) {
            int[] r = rect(selected);
            float newW = (float) Math.max(10, mx - r[0]);
            selected.setScalePct(Math.round(newW / w * 10f) * 10);
            changed = true;
            return true;
        }
        if (moving) {
            float s = selected.scalePct() / 100f;
            int pw = Math.round(w * s), ph = Math.round(h * s);
            int x = Math.round((float) mx - grabDX), y = Math.round((float) my - grabDY);
            guideV = Math.abs(x + pw / 2 - width / 2) <= SNAP;
            guideH = Math.abs(y + ph / 2 - height / 2) <= SNAP;
            if (guideV) x = width / 2 - pw / 2;
            if (guideH) y = height / 2 - ph / 2;
            if (Math.abs(x) <= SNAP) x = 0;
            if (Math.abs(y) <= SNAP) y = 0;
            if (Math.abs(x + pw - width) <= SNAP) x = width - pw;
            if (Math.abs(y + ph - height) <= SNAP) y = height - ph;
            selected.placeAtPixels(x, y, width, height, w, h);
            changed = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        boolean wasBar = Btn.upAll(bar, mx, my);
        moving = false;
        scaling = false;
        guideV = guideH = false;
        if (changed) {
            ProfileManager.save();
            changed = false;
        }
        return wasBar;
    }

    @Override
    public void close() { client.setScreen(parent); }

    @Override
    public void removed() { ProfileManager.save(); }
}
