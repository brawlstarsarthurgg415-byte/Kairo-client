package dev.kairo.ui.pane;

import dev.kairo.core.KModule;
import dev.kairo.core.ModuleManager;
import dev.kairo.core.ProfileManager;
import dev.kairo.ui.Btn;
import dev.kairo.ui.Gfx;
import dev.kairo.ui.Icons;
import dev.kairo.ui.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class HomePane extends Pane {
    private final List<Btn> buttons = new ArrayList<>();

    public HomePane(Shell shell) {
        buttons.add(new Btn("Editor de HUD", shell::openLayoutEditor).icon(Icons.LAYOUT).primary());
        buttons.add(new Btn("Biblioteca de elementos", shell::openLibrary).icon(Icons.GRID));
        buttons.add(new Btn("Perfis", () -> shell.openTab(6)).icon(Icons.PROFILE));
        buttons.add(new Btn("Módulos de PvP", () -> shell.openTab(1)).icon(Icons.SWORD));
    }

    @Override
    protected void relayout() {
        int top = y + 92;
        int cols = w >= 300 ? 2 : 1;
        int bw = (w - (cols - 1) * 6) / cols;
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).bounds(x + (i % cols) * (bw + 6), top + (i / cols) * 28, bw, 24);
        }
    }

    @Override
    public void render(DrawContext c, MinecraftClient mc, int mx, int my) {
        var tr = mc.textRenderer;
        String user = mc.getSession().getUsername();
        Gfx.textScaled(c, tr, "Bem-vindo de volta, " + user + "!", x + 2, y + 2, 1.25f, Theme.TEXT, false);
        // cartao hero
        int hy = y + 20, hh = 64;
        Gfx.card(c, x, hy, w, hh, Theme.SURFACE2, Theme.BORDER);
        c.fillGradient(x + 1, hy + 1, x + w / 2, hy + hh - 1, Theme.accentA(0.18f), 0x00000000);
        Gfx.textScaled(c, tr, "KAIRO", x + 12, hy + 10, 2.6f, Theme.accent(), true);
        Gfx.text(c, tr, "Cliente PvP · personalização total", x + 14, hy + 40, Theme.MUTED);
        int active = 0, total = 0;
        for (KModule m : ModuleManager.all()) {
            if (m.isCore()) continue;
            total++;
            if (m.isEnabled()) active++;
        }
        String l1 = "Perfil: " + ProfileManager.active();
        String l2 = active + " de " + total + " módulos ativos";
        Gfx.text(c, tr, l1, x + w - 10 - tr.getWidth(l1), hy + 14, Theme.TEXT);
        Gfx.text(c, tr, l2, x + w - 10 - tr.getWidth(l2), hy + 28, Theme.MUTED);
        for (Btn b : buttons) b.render(c, tr, mx, my);
    }

    @Override public boolean down(double mx, double my) { return Btn.downAll(buttons, mx, my); }

    @Override public boolean up(double mx, double my) { return Btn.upAll(buttons, mx, my); }
}
