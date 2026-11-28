package dev.kairo.gui;

import dev.kairo.core.Category;
import dev.kairo.core.KModule;
import dev.kairo.core.ModuleManager;
import dev.kairo.core.ProfileManager;
import dev.kairo.ui.Backdrop;
import dev.kairo.ui.Gfx;
import dev.kairo.ui.Icons;
import dev.kairo.ui.Theme;
import dev.kairo.ui.pane.DetailPane;
import dev.kairo.ui.pane.HomePane;
import dev.kairo.ui.pane.ModulesPane;
import dev.kairo.ui.pane.Pane;
import dev.kairo.ui.pane.ProfilesPane;
import dev.kairo.ui.pane.Shell;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/** Central do Kairo: trilho de navegacao a esquerda, busca no topo e painel de conteudo. */
public class KairoMenuScreen extends Screen implements Shell {
    private static final String[] LABELS = {"Início", "PvP", "HUD", "Visual", "Desempenho", "Utilidades", "Perfis"};
    private static final int[] ICONS = {Icons.HOME, Icons.SWORD, Icons.LAYOUT, Icons.EYE, Icons.BOLT, Icons.TOOLS, Icons.PROFILE};
    private static final Category[] CATS = {null, Category.PVP, Category.HUD, Category.VISUAL, Category.PERFORMANCE,
        Category.UTILITY, null};
    private static int lastTab = 0;

    private final Screen parent;
    private int tab = lastTab;
    private Pane pane;
    private KModule detail;
    private String search = "";
    private boolean searchFocus;
    private boolean dirty;

    private int railX, railY, railW, railH, itemH;
    private int contentX, contentY, contentW, contentH;
    private int searchX, searchY, searchW;

    public KairoMenuScreen(Screen parent) {
        super(Text.literal("Kairo Client"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        layout();
        rebuild();
    }

    private void layout() {
        railX = 6;
        railY = 6;
        railW = Math.max(66, Math.min(100, width / 5));
        railH = height - 12;
        int items = LABELS.length + 3;
        itemH = Math.max(18, Math.min(26, (railH - 16) / items));
        contentX = railX + railW + 8;
        contentY = 38;
        contentW = width - contentX - 8;
        contentH = height - contentY - 8;
        searchW = Math.min(150, contentW / 2);
        searchX = width - 8 - searchW;
        searchY = 8;
    }

    private void rebuild() {
        Pane p;
        if (!search.isEmpty()) {
            List<KModule> hits = new ArrayList<>();
            String q = search.toLowerCase(Locale.ROOT);
            for (KModule m : ModuleManager.all()) {
                if (m.isCore()) continue;
                if (m.name.toLowerCase(Locale.ROOT).contains(q) || m.description.toLowerCase(Locale.ROOT).contains(q)) hits.add(m);
            }
            p = new ModulesPane("Resultados para \"" + search + "\"", hits, this);
        } else if (detail != null) {
            p = new DetailPane(detail, this, () -> { detail = null; rebuild(); });
        } else if (tab == 0) {
            p = new HomePane(this);
        } else if (tab == 6) {
            p = new ProfilesPane(this);
        } else {
            p = new ModulesPane(LABELS[tab], ModuleManager.byCategory(CATS[tab]), this);
        }
        pane = p;
        pane.bounds(contentX, contentY, contentW, contentH);
    }

    // ---------- Shell ----------
    @Override
    public void openModule(KModule m) {
        detail = m;
        search = "";
        rebuild();
    }

    @Override
    public void openTab(int t) {
        tab = t;
        lastTab = t;
        detail = null;
        search = "";
        rebuild();
    }

    @Override
    public void openLayoutEditor() { client.setScreen(new LayoutEditorScreen(this)); }

    @Override
    public void openLibrary() { client.setScreen(new ElementLibraryScreen(this)); }

    @Override
    public void changed() { dirty = true; }

    // ---------- desenho ----------
    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        if (client.world == null) Backdrop.draw(c, width, height);
        else c.fill(0, 0, width, height, 0xC8070910);

        Gfx.card(c, railX, railY, railW, railH, Theme.SURFACE, Theme.BORDER);
        for (int i = 0; i < LABELS.length + 3; i++) {
            int iy = railItemY(i);
            boolean sel = i < LABELS.length && i == tab && detail == null && search.isEmpty();
            boolean hover = Gfx.in(mx, my, railX + 4, iy, railW - 8, itemH - 2);
            if (sel) Gfx.roundRect(c, railX + 4, iy, railW - 8, itemH - 2, 4, Theme.accentA(0.2f));
            else if (hover) Gfx.roundRect(c, railX + 4, iy, railW - 8, itemH - 2, 4, Theme.SURFACE3);
            int col = sel ? Theme.accent() : Theme.TEXT;
            int icon; String label;
            if (i < LABELS.length) { icon = ICONS[i]; label = LABELS[i]; }
            else if (i == LABELS.length) { icon = Icons.LAYOUT; label = "Editor HUD"; }
            else if (i == LABELS.length + 1) { icon = Icons.GRID; label = "Biblioteca"; }
            else { icon = Icons.POWER; label = "Fechar"; }
            if (sel) c.fill(railX + 2, iy + 3, railX + 4, iy + itemH - 5, Theme.accent());
            Icons.draw(c, icon, railX + 9, iy + (itemH - 2 - 12) / 2, 1, col);
            Gfx.text(c, textRenderer, Gfx.ellipsize(textRenderer, label, railW - 32), railX + 25, iy + (itemH - 2 - 8) / 2, col);
        }

        Gfx.textScaled(c, textRenderer, "KAIRO", contentX, 10, 1.6f, Theme.accent(), true);
        Gfx.text(c, textRenderer, "v" + dev.kairo.KairoClient.version(), contentX + 62, 16, Theme.MUTED);

        Gfx.roundRect(c, searchX, searchY, searchW, 18, 4, Theme.SURFACE2);
        Gfx.roundOutline(c, searchX, searchY, searchW, 18, 4, searchFocus ? Theme.accent() : Theme.BORDER);
        Icons.draw(c, Icons.SEARCH, searchX + 5, searchY + 3, 1, Theme.MUTED);
        String shown = search.isEmpty() ? "Buscar módulos" : search + (searchFocus && (System.currentTimeMillis() / 500) % 2 == 0 ? "_" : "");
        Gfx.text(c, textRenderer, Gfx.ellipsize(textRenderer, shown, searchW - 26), searchX + 22, searchY + 5,
            search.isEmpty() ? Theme.MUTED : Theme.TEXT);

        pane.render(c, client, mx, my);
    }

    private int railItemY(int i) {
        int y = railY + 8 + i * itemH;
        return i >= LABELS.length ? y + 8 : y; // espaco antes das acoes
    }

    // ---------- entrada ----------
    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        searchFocus = Gfx.in(mx, my, searchX, searchY, searchW, 18);
        if (searchFocus) return true;
        for (int i = 0; i < LABELS.length + 3; i++) {
            if (Gfx.in(mx, my, railX + 4, railItemY(i), railW - 8, itemH - 2)) {
                if (i < LABELS.length) openTab(i);
                else if (i == LABELS.length) openLayoutEditor();
                else if (i == LABELS.length + 1) openLibrary();
                else close();
                return true;
            }
        }
        return pane.down(mx, my);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        return pane.drag(mx, my);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        boolean r = pane.up(mx, my);
        if (dirty) {
            ProfileManager.save();
            dirty = false;
        }
        return r;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double h, double v) {
        return pane.scroll(mx, my, v);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchFocus && search.length() < 24 && chr >= 32) {
            search += chr;
            rebuild();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int key, int scan, int mods) {
        if (searchFocus && key == 259 && !search.isEmpty()) { // backspace
            search = search.substring(0, search.length() - 1);
            rebuild();
            return true;
        }
        if (key == 256) { // esc
            if (!search.isEmpty()) { search = ""; searchFocus = false; rebuild(); return true; }
            if (detail != null) { detail = null; rebuild(); return true; }
        }
        return super.keyPressed(key, scan, mods);
    }

    @Override
    public void close() { client.setScreen(parent); }

    @Override
    public void removed() {
        dev.kairo.modules.KeystrokesModule.previewReleaseAll();
        ProfileManager.save();
    }
}
