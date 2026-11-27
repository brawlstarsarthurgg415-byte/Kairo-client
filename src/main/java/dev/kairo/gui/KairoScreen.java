package dev.kairo.gui;

import dev.kairo.core.AppearanceManager;
import dev.kairo.core.Category;
import dev.kairo.core.KModule;
import dev.kairo.core.ModuleManager;
import dev.kairo.core.ProfileManager;
import dev.kairo.gui.widgets.KButton;
import dev.kairo.gui.widgets.ModuleListPanel;
import dev.kairo.gui.widgets.SettingsPanel;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/**
 * Menu principal do Kairo: abas de categoria, busca de modulos, lista rolavel
 * com cartoes, painel de configuracoes do modulo selecionado, gerenciamento
 * de perfis, aparência global e acesso ao editor de HUD.
 * Layout adaptativo para telas pequenas (toque) e grandes (desktop).
 */
public class KairoScreen extends Screen {
    private static KModule selected;
    private static Category tab;

    private ModuleListPanel listPanel;
    private SettingsPanel settingsPanel;
    private TextFieldWidget search;

    public KairoScreen() {
        super(Text.literal("Kairo Client"));
    }

    @Override
    protected void init() {
        int margin = 8;
        int headerH = 24;
        int bottomH = 42;
        int top = margin + headerH + 4;
        int listBottom = height - bottomH - 4;

        int catW = width < 360 ? 70 : 92;
        int listW = Math.max(120, Math.min(width / 3, 210));
        int listX = margin + catW + 4;
        int rightX = listX + listW + 6;
        int rightW = Math.max(100, width - rightX - margin);

        // Cabecalho: titulo + busca.
        int searchW = Math.min(180, Math.max(110, width / 3));
        search = new TextFieldWidget(textRenderer, width - margin - searchW, margin + 4, searchW, 16, Text.literal("Buscar"));
        search.setSuggestion("Buscar módulo…");
        search.setChangedListener(q -> {
            if (listPanel != null) listPanel.setFilter(currentTab(), q);
        });
        addDrawableChild(search);

        // Abas de categoria.
        int ty = top;
        for (Category c : Category.values()) {
            if (ModuleManager.byCategory(c).isEmpty()) continue;
            Category target = c;
            addDrawableChild(new KButton(c.label, margin, ty, catW - 2, 18, btn -> {
                tab = target;
                if (listPanel != null) listPanel.setFilter(currentTab(), search.getText());
                clearAndInit();
            }));
            ty += 20;
        }
        KButton hudEditor = new KButton("Editor HUD", margin, listBottom - 18, catW - 2, 18,
            btn -> {
                ProfileManager.save();
                client.setScreen(new HudEditorScreen());
            });
        addDrawableChild(hudEditor);

        // Lista de modulos.
        listPanel = new ModuleListPanel(listX, top, listW, listBottom - top, m -> {
            selected = m;
            if (settingsPanel != null) settingsPanel.setModule(m);
            clearAndInit();
        });
        listPanel.setFilter(currentTab(), search == null ? "" : search.getText());
        listPanel.setSelected(selected);
        addDrawableChild(listPanel);

        // Painel de configuracoes.
        int toggleY = top + 2;
        if (selected != null) {
            KButton toggle = new KButton(selected.isEnabled() ? "DESATIVAR" : "ATIVAR",
                rightX, toggleY, rightW, 18, btn -> {
                    selected.toggle();
                    clearAndInit();
                });
            addDrawableChild(toggle);
            KButton reset = new KButton("Restaurar padrões", rightX, toggleY + 20, rightW, 16, btn -> {
                selected.resetDefaults();
                clearAndInit();
            });
            addDrawableChild(reset);
            settingsPanel = new SettingsPanel(rightX, toggleY + 40, rightW,
                listBottom - (toggleY + 40), () -> ProfileManager.save());
            settingsPanel.setModule(selected);
            addDrawableChild(settingsPanel);
        }

        // Barra inferior: perfis, aparência / importacao, exportacao, editor, fechar.
        int row1 = height - bottomH + 2;
        int row2 = row1 + 20;
        int half = (width - margin * 2 - 6) / 2;
        addDrawableChild(new KButton("Perfil: " + ProfileManager.active(), margin, row1, half, 18,
            btn -> { ProfileManager.next(); clearAndInit(); }));
        addDrawableChild(new KButton("Novo perfil", margin + half + 6, row1, half, 18,
            btn -> { ProfileManager.createFromCurrent(); clearAndInit(); }));
        addDrawableChild(new KButton("Cor: " + String.format("#%06X", AppearanceManager.accentRgb()),
            margin, row2, half, 18, btn -> { AppearanceManager.cycleAccent(true); clearAndInit(); }));
        addDrawableChild(new KButton(AppearanceManager.menuAnimations() ? "Animações: ligadas" : "Animações: desligadas",
            margin + half + 6, row2, half, 18, btn -> {
                AppearanceManager.setMenuAnimations(!AppearanceManager.menuAnimations());
                AppearanceManager.save();
                clearAndInit();
            }));
        addDrawableChild(new KButton("Exportar", width - margin - 166, row1, 80, 18, btn -> {
            java.nio.file.Path p = ProfileManager.exportActive();
            message("Perfil exportado: " + (p == null ? "falhou" : p.getFileName()));
        }));
        addDrawableChild(new KButton("Importar", width - margin - 82, row1, 80, 18,
            btn -> client.setScreen(new ImportScreen(this))));
        addDrawableChild(new KButton("Fechar", width - margin - 166, row2, 166, 18, btn -> close()));
    }

    private boolean searchActive() {
        return search != null && !search.getText().trim().isEmpty();
    }

    private Category currentTab() {
        if (searchActive()) return null; // busca ignora a aba
        if (tab != null && !ModuleManager.byCategory(tab).isEmpty()) return tab;
        for (Category c : Category.values()) if (!ModuleManager.byCategory(c).isEmpty()) return c;
        return null;
    }

    private void message(String s) {
        if (client != null && client.player != null) {
            client.player.sendMessage(Text.literal("Kairo: " + s), true);
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, KairoTheme.BG);
        super.render(ctx, mouseX, mouseY, delta);

        ctx.drawText(textRenderer, "KAIRO", 12, 10, KairoTheme.TEXT, true);
        ctx.drawText(textRenderer, "CLIENT  /  PVP TOOLS", 56, 11, KairoTheme.MUTED, false);
        int tabY = 8 + 24 + 4;
        int catW = width < 360 ? 70 : 92;
        for (Category c : Category.values()) {
            if (ModuleManager.byCategory(c).isEmpty()) continue;
            if (currentTab() == c) {
                ctx.fill(8, tabY + 14, 8 + catW - 2, tabY + 15, KairoTheme.accent());
                break;
            }
            tabY += 20;
        }

        if (selected != null) {
            int rightX = Math.max(120, Math.min(width / 3, 210)) + (width < 360 ? 70 : 92) + 8 + 4 + 6;
            ctx.drawText(textRenderer, selected.name.toUpperCase(), rightX + 2, 34, KairoTheme.TEXT, true);
            ctx.drawText(textRenderer, selected.description, rightX + 2, 45, KairoTheme.MUTED, false);
            String status = selected.isEnabled() ? "● ATIVO" : "○ DESATIVADO";
            ctx.drawText(textRenderer, status, rightX + 2, height - 52,
                selected.isEnabled() ? KairoTheme.accent() : KairoTheme.MUTED, false);
        }
        ctx.drawText(textRenderer, "KAIRO CLIENT • 1.21.1 • " + ProfileManager.active(), 12, height - 12, KairoTheme.MUTED, false);
    }

    @Override
    public void removed() {
        ProfileManager.save();
        AppearanceManager.save();
    }

    /** Tela de importacao: lista os perfis exportados. */
    private static class ImportScreen extends Screen {
        private final Screen parent;

        ImportScreen(Screen parent) {
            super(Text.literal("Importar perfil"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            int y = 40;
            List<String> exports = ProfileManager.exports();
            for (String name : exports) {
                addDrawableChild(new KButton("Importar: " + name, width / 2 - 100, y, 200, 20, btn -> {
                    String imported = ProfileManager.importAsNew(name);
                    if (client != null && client.player != null) {
                        client.player.sendMessage(Text.literal("Kairo: perfil importado: " + imported), true);
                    }
                    client.setScreen(parent);
                }));
                y += 24;
            }
            addDrawableChild(new KButton("Voltar", width / 2 - 100, y + 6, 200, 20, btn -> client.setScreen(parent)));
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
            ctx.fill(0, 0, width, height, KairoTheme.BG);
            super.render(ctx, mouseX, mouseY, delta);
            ctx.drawCenteredTextWithShadow(textRenderer, "IMPORTAR PERFIL", width / 2, 20, KairoTheme.TEXT);
            if (ProfileManager.exports().isEmpty()) {
                ctx.drawCenteredTextWithShadow(textRenderer, "Nenhum perfil exportado ainda.", width / 2, height / 2, KairoTheme.MUTED);
            }
        }
    }
}
