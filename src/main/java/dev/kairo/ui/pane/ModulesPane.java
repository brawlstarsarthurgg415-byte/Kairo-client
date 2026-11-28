package dev.kairo.ui.pane;

import dev.kairo.core.KModule;
import dev.kairo.ui.ModuleGridList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class ModulesPane extends Pane {
    private final String title;
    private final ModuleGridList grid;

    public ModulesPane(String title, List<KModule> modules, Shell shell) {
        this.title = title;
        this.grid = new ModuleGridList(modules, ModuleGridList.Style.INFO, shell::openModule, shell::changed);
    }

    @Override
    protected void relayout() { grid.bounds(x, y + 16, w, h - 16); }

    @Override
    public void render(DrawContext c, MinecraftClient mc, int mx, int my) {
        c.drawText(mc.textRenderer, title, x + 2, y + 2, dev.kairo.ui.Theme.MUTED, false);
        if (grid.contentHeight() == 0) {
            c.drawText(mc.textRenderer, "Nenhum módulo encontrado.", x + 2, y + 24, dev.kairo.ui.Theme.MUTED, false);
        }
        grid.render(c, mc.textRenderer, mx, my);
    }

    @Override public boolean down(double mx, double my) { return grid.mouseDown(mx, my); }

    @Override public boolean drag(double mx, double my) { return grid.mouseDrag(mx, my); }

    @Override public boolean up(double mx, double my) { return grid.mouseUp(mx, my); }

    @Override public boolean scroll(double mx, double my, double a) { return grid.mouseScroll(mx, my, a); }
}
