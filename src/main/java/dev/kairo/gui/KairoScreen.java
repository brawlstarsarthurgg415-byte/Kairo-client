package dev.kairo.gui;

import dev.kairo.core.Category;
import dev.kairo.core.KModule;
import dev.kairo.core.ModuleManager;
import dev.kairo.core.ProfileManager;
import dev.kairo.core.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Painel principal do Kairo: layout adaptavel com navegacao e cartao de configuracoes. */
public class KairoScreen extends Screen {
    private static KModule selected;

    private static final int BG = 0xE8101420;
    private static final int PANEL = 0xE91B2231;
    private static final int PANEL_ALT = 0xD9232D40;
    private static final int ACCENT = 0xFF55D6BE;
    private static final int MUTED = 0xFF9AA8BC;

    public KairoScreen() {
        super(Text.literal("Kairo Client"));
    }

    @Override
    protected void init() {
        int margin = 12;
        int top = 42;
        int sidebarW = Math.min(172, Math.max(112, width / 3));
        int rightX = margin + sidebarW + 10;
        int rightW = Math.max(100, width - rightX - margin);
        int buttonW = sidebarW - 12;
        int y = top;

        for (Category category : Category.values()) {
            boolean hasModules = !ModuleManager.byCategory(category).isEmpty();
            if (!hasModules) continue;
            y += 4;
            for (KModule module : ModuleManager.byCategory(category)) {
                String state = module.isEnabled() ? "● " : "○ ";
                String label = state + module.name;
                addDrawableChild(ButtonWidget.builder(Text.literal(label), b -> {
                    selected = module;
                    clearAndInit();
                }).dimensions(margin + 6, y, buttonW, 20).build());
                y += 22;
            }
        }

        int settingY = top + 30;
        if (selected != null) {
            addDrawableChild(ButtonWidget.builder(
                Text.literal(selected.isEnabled() ? "DESATIVAR MÓDULO" : "ATIVAR MÓDULO"), b -> {
                    selected.toggle();
                    clearAndInit();
                }).dimensions(rightX + 8, settingY, rightW - 16, 20).build());
            settingY += 26;

            for (Setting setting : selected.settings()) {
                int rowW = rightW - 16;
                if (setting.kind == Setting.Kind.INT) {
                    addDrawableChild(ButtonWidget.builder(Text.literal("−"), b -> {
                        setting.cycle(false);
                        clearAndInit();
                    }).dimensions(rightX + 8, settingY, 20, 20).build());
                    addDrawableChild(ButtonWidget.builder(
                        Text.literal(setting.label + ": " + setting.display()), b -> {
                            setting.cycle(true);
                            clearAndInit();
                        }).dimensions(rightX + 32, settingY, Math.max(64, rowW - 24), 20).build());
                } else {
                    addDrawableChild(ButtonWidget.builder(
                        Text.literal(setting.label + ": " + setting.display()), b -> {
                            setting.cycle(true);
                            clearAndInit();
                        }).dimensions(rightX + 8, settingY, rowW, 20).build());
                }
                settingY += 23;
            }
            addDrawableChild(ButtonWidget.builder(Text.literal("Restaurar padrões"), b -> {
                selected.resetDefaults();
                clearAndInit();
            }).dimensions(rightX + 8, Math.min(settingY + 3, height - 52), rightW - 16, 20).build());
        }

        int bottomY = height - 28;
        int profileW = Math.min(138, sidebarW - 12);
        addDrawableChild(ButtonWidget.builder(Text.literal("Perfil: " + ProfileManager.active()), b -> {
            ProfileManager.next();
            clearAndInit();
        }).dimensions(margin + 6, bottomY, profileW, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("＋"), b -> {
            ProfileManager.createFromCurrent();
            clearAndInit();
        }).dimensions(margin + 10 + profileW, bottomY, 22, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Fechar"), b -> close())
            .dimensions(width - 76, bottomY, 64, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int margin = 12;
        int sidebarW = Math.min(172, Math.max(112, width / 3));
        int rightX = margin + sidebarW + 10;

        ctx.fill(0, 0, width, height, BG);
        ctx.fill(margin, 34, margin + sidebarW, height - 34, PANEL);
        ctx.fill(rightX, 34, width - margin, height - 34, PANEL);
        ctx.fill(margin, 8, width - margin, 29, PANEL_ALT);
        ctx.fill(margin + 6, 34, margin + sidebarW - 6, 36, ACCENT);
        ctx.fill(rightX + 8, 34, width - margin - 8, 36, ACCENT);

        super.render(ctx, mouseX, mouseY, delta);

        ctx.drawText(textRenderer, "KAIRO", margin + 8, 14, 0xFFFFFFFF, true);
        ctx.drawText(textRenderer, "CLIENT  /  PVP TOOLS", margin + 58, 15, MUTED, false);
        ctx.drawText(textRenderer, "MÓDULOS", margin + 8, 40, ACCENT, true);

        if (selected != null) {
            ctx.drawText(textRenderer, selected.name.toUpperCase(), rightX + 8, 43, 0xFFFFFFFF, true);
            ctx.drawText(textRenderer, selected.description, rightX + 8, 57, MUTED, false);
            String status = selected.isEnabled() ? "ATIVO" : "DESATIVADO";
            ctx.drawText(textRenderer, "STATUS: " + status, rightX + 8, height - 44,
                selected.isEnabled() ? ACCENT : MUTED, true);
        } else {
            ctx.drawText(textRenderer, "CENTRAL DE CONFIGURAÇÕES", rightX + 8, 43, 0xFFFFFFFF, true);
            ctx.drawText(textRenderer, "Selecione um módulo para ver suas opções.", rightX + 8, 60, MUTED, false);
            ctx.drawText(textRenderer, "Personalize HUD, mira e ferramentas PvP.", rightX + 8, 73, MUTED, false);
        }
        ctx.drawText(textRenderer, "KAIRO CLIENT  •  1.21.1", margin + 8, height - 15, MUTED, false);
    }

    @Override
    public void removed() {
        ProfileManager.save();
    }
}
