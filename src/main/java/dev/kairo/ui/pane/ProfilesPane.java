package dev.kairo.ui.pane;

import dev.kairo.core.ProfileManager;
import dev.kairo.ui.Btn;
import dev.kairo.ui.Gfx;
import dev.kairo.ui.ScrollList;
import dev.kairo.ui.Theme;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public final class ProfilesPane extends Pane {
    private final Shell shell;
    private final List<Btn> buttons = new ArrayList<>();
    private List<String> names = ProfileManager.list();
    private String status = "";
    private final ScrollList list = new ScrollList() {
        @Override protected int rowCount() { return names.size(); }

        @Override protected int rowHeight(int row) { return 28; }

        @Override
        protected void renderRow(DrawContext c, TextRenderer tr, int row, int rx, int ry, int rw, int mx, int my) {
            String n = names.get(row);
            boolean active = n.equals(ProfileManager.active());
            Gfx.card(c, rx, ry + 2, rw - 4, 24, Gfx.in(mx, my, rx, ry, rw, 28) ? Theme.SURFACE3 : Theme.SURFACE2,
                active ? Theme.accentA(0.6f) : Theme.BORDER);
            Gfx.text(c, tr, n, rx + 8, ry + 10, Theme.TEXT);
            String tag = active ? "ATIVO" : "Ativar";
            Gfx.text(c, tr, tag, rx + rw - 12 - tr.getWidth(tag), ry + 10, active ? Theme.accent() : Theme.MUTED);
        }

        @Override
        protected void onTap(int row, int lx, int ly) {
            String n = names.get(row);
            if (!n.equals(ProfileManager.active())) {
                ProfileManager.switchTo(n);
                status = "Perfil ativo: " + n;
                shell.changed();
            }
        }
    };

    public ProfilesPane(Shell shell) {
        this.shell = shell;
        buttons.add(new Btn("Novo perfil", () -> {
            status = "Criado: " + ProfileManager.createFromCurrent();
            names = ProfileManager.list();
        }).primary());
        buttons.add(new Btn("Exportar", () -> {
            Path p = ProfileManager.exportActive();
            status = p == null ? "Falha ao exportar" : "Exportado em " + p.getParent().getParent().getFileName() + "/exports";
        }));
        buttons.add(new Btn("Importar", () -> {
            int n = ProfileManager.importAll();
            names = ProfileManager.list();
            Path dir = ProfileManager.importDir();
            status = n > 0 ? n + " perfil(is) importado(s)" : "Nada novo. Coloque .json em config/kairo/" + (dir == null ? "import" : dir.getFileName());
        }));
        buttons.add(new Btn("Restaurar padrão", () -> {
            ProfileManager.resetActive();
            status = "Perfil restaurado ao padrão";
            shell.changed();
        }));
    }

    @Override
    protected void relayout() {
        int bw = (w - 18) / 4;
        for (int i = 0; i < buttons.size(); i++) buttons.get(i).bounds(x + i * (bw + 6), y, bw, 20);
        list.bounds(x, y + 40, w, h - 40);
    }

    @Override
    public void render(DrawContext c, MinecraftClient mc, int mx, int my) {
        for (Btn b : buttons) b.render(c, mc.textRenderer, mx, my);
        Gfx.text(c, mc.textRenderer, status.isEmpty() ? "Perfis guardam todos os módulos e posições." : status,
            x + 2, y + 27, Theme.MUTED);
        list.render(c, mc.textRenderer, mx, my);
    }

    @Override public boolean down(double mx, double my) { return Btn.downAll(buttons, mx, my) || list.mouseDown(mx, my); }

    @Override public boolean drag(double mx, double my) { return list.mouseDrag(mx, my); }

    @Override public boolean up(double mx, double my) { return Btn.upAll(buttons, mx, my) | list.mouseUp(mx, my); }

    @Override public boolean scroll(double mx, double my, double a) { return list.mouseScroll(mx, my, a); }
}
