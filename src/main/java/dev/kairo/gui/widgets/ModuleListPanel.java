package dev.kairo.gui.widgets;

import dev.kairo.core.Category;
import dev.kairo.core.KModule;
import dev.kairo.core.ModuleManager;
import dev.kairo.gui.KairoTheme;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.gui.DrawContext;

/**
 * Lista rolavel de modulos com cartoes: estado de ativacao, nome e categoria.
 * Suporta filtro por categoria e busca por texto.
 */
public class ModuleListPanel extends KScrollPanel {
    private static final int ROW_H = 24;

    private final Consumer<KModule> onSelect;
    private Category filter;
    private String query = "";
    private KModule selected;
    private final List<KModule> visible = new ArrayList<>();

    public ModuleListPanel(int x, int y, int w, int h, Consumer<KModule> onSelect) {
        super(x, y, w, h);
        this.onSelect = onSelect;
        refilter();
    }

    public void setFilter(Category category, String search) {
        this.filter = category;
        this.query = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        refilter();
    }

    public void setSelected(KModule module) {
        this.selected = module;
    }

    private void refilter() {
        visible.clear();
        for (KModule m : ModuleManager.all()) {
            if (filter != null && m.category != filter) continue;
            if (!query.isEmpty()) {
                String hay = (m.name + " " + m.id + " " + m.category.label).toLowerCase(Locale.ROOT);
                if (!hay.contains(query)) continue;
            }
            visible.add(m);
        }
        scrollTo(scroll);
    }

    @Override
    protected int contentHeight() {
        return visible.size() * ROW_H + 4;
    }

    @Override
    protected void drawRows(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int x = getX() + 3;
        int w = getWidth() - 8;
        int y = getY() + 2;
        if (visible.isEmpty()) {
            ctx.drawCenteredTextWithShadow(textRenderer, query.isEmpty() ? "Sem módulos" : "Nada encontrado",
                getX() + getWidth() / 2, getY() + getHeight() / 2, KairoTheme.MUTED);
            return;
        }
        for (KModule m : visible) {
            boolean sel = m == selected;
            int bg = sel ? KairoTheme.CARD_ACTIVE : KairoTheme.CARD;
            ctx.fill(x, y, x + w, y + ROW_H - 2, bg);
            // Barra lateral de selecao.
            if (sel) ctx.fill(x, y, x + 2, y + ROW_H - 2, KairoTheme.accent());
            // Indicador de estado (ativo/inativo).
            int dot = m.isEnabled() ? KairoTheme.accent() : 0xFF4A5568;
            ctx.fill(x + 7, y + 9, x + 11, y + 13, dot);
            int textColor = m.isEnabled() ? KairoTheme.TEXT : KairoTheme.MUTED;
            String name = fit(m.name, w - 18);
            ctx.drawText(textRenderer, name, x + 16, y + 4, textColor, false);
            ctx.drawText(textRenderer, m.category.label, x + 16, y + 14, KairoTheme.MUTED, false);
            y += ROW_H;
        }
    }

    /** Encurta um texto com reticencias para caber na largura. */
    private String fit(String s, int widthPx) {
        if (textRenderer.getWidth(s) <= widthPx) return s;
        String ell = "…";
        while (!s.isEmpty() && textRenderer.getWidth(s + ell) > widthPx) s = s.substring(0, s.length() - 1);
        return s + ell;
    }

    @Override
    protected boolean rowClicked(double mouseX, double localY, int button) {
        int idx = (int) (localY / ROW_H);
        if (idx < 0 || idx >= visible.size()) return false;
        KModule m = visible.get(idx);
        if (button == 1) {
            m.toggle(); // clique longo/direito alterna rapidamente
        }
        selected = m;
        if (onSelect != null) onSelect.accept(m);
        return true;
    }
}
