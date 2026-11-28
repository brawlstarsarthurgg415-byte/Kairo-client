package dev.kairo.gui;

import dev.kairo.core.KModule;
import dev.kairo.core.ProfileManager;
import dev.kairo.modules.HudModule;
import dev.kairo.ui.Backdrop;
import dev.kairo.ui.Btn;
import dev.kairo.ui.Gfx;
import dev.kairo.ui.Icons;
import dev.kairo.ui.ModuleGridList;
import dev.kairo.ui.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/** Element Library: tela separada do editor para adicionar e remover elementos do HUD. */
public class ElementLibraryScreen extends Screen {
    private final Screen parent;
    private final List<Btn> buttons = new ArrayList<>();
    private ModuleGridList grid;
    private boolean dirty;

    public ElementLibraryScreen(Screen parent) {
        super(Text.literal("Biblioteca de elementos"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        buttons.clear();
        buttons.add(new Btn("Voltar", this::close).icon(Icons.BACK));
        buttons.add(new Btn("Editar layout", () -> client.setScreen(new LayoutEditorScreen(parent))).primary()
            .icon(Icons.LAYOUT));
        buttons.get(0).bounds(8, 8, 64, 20);
        buttons.get(1).bounds(width - 8 - 100, 8, 100, 20);
        List<KModule> mods = new ArrayList<>();
        for (HudModule m : dev.kairo.core.ModuleManager.hudModules()) mods.add(m);
        grid = new ModuleGridList(mods, ModuleGridList.Style.PREVIEW, m -> { }, () -> dirty = true);
        grid.bounds(8, 52, width - 16, height - 60);
    }

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        if (client.world == null) Backdrop.draw(c, width, height);
        else c.fill(0, 0, width, height, 0xC8070910);
        Gfx.textScaled(c, textRenderer, "Biblioteca de elementos", 80, 12, 1.2f, Theme.TEXT, false);
        Gfx.text(c, textRenderer, "Adicione ou remova elementos. Para posicionar, use o Editor de layout.", 8, 36, Theme.MUTED);
        for (Btn b : buttons) b.render(c, textRenderer, mx, my);
        grid.render(c, textRenderer, mx, my);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        return Btn.downAll(buttons, mx, my) || grid.mouseDown(mx, my);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) { return grid.mouseDrag(mx, my); }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        boolean r = grid.mouseUp(mx, my);
        if (dirty) { ProfileManager.save(); dirty = false; }
        return Btn.upAll(buttons, mx, my) || r;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double h, double v) { return grid.mouseScroll(mx, my, v); }

    @Override
    public void close() { client.setScreen(parent); }

    @Override
    public void removed() { ProfileManager.save(); }
}
