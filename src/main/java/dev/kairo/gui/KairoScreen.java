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

/** Central de mods: lista de modulos a esquerda, configuracoes do modulo selecionado a direita. */
public class KairoScreen extends Screen {
    private static KModule selected;

    public KairoScreen() {
        super(Text.literal("Kairo Client"));
    }

    @Override
    protected void init() {
        int left = 16;
        int top = 36;
        int y = top;
        for (Category c : Category.values()) {
            for (KModule m : ModuleManager.byCategory(c)) {
                String label = (m.isEnabled() ? "[ON] " : "[OFF] ") + m.name;
                addDrawableChild(ButtonWidget.builder(Text.literal(label), b -> {
                    selected = m;
                    clearAndInit();
                }).dimensions(left, y, 140, 20).build());
                y += 22;
            }
        }

        int rx = left + 160;
        if (selected != null) {
            int ry = top + 14;
            addDrawableChild(ButtonWidget.builder(
                Text.literal(selected.isEnabled() ? "Módulo: ATIVO" : "Módulo: DESATIVADO"), b -> {
                    selected.toggle();
                    clearAndInit();
                }).dimensions(rx, ry, 200, 20).build());
            ry += 26;
            for (Setting s : selected.settings()) {
                if (s.kind == Setting.Kind.INT) {
                    addDrawableChild(ButtonWidget.builder(Text.literal("-"), b -> {
                        s.cycle(false);
                        clearAndInit();
                    }).dimensions(rx, ry, 20, 20).build());
                    addDrawableChild(ButtonWidget.builder(Text.literal(s.label + ": " + s.display()), b -> {
                        s.cycle(true);
                        clearAndInit();
                    }).dimensions(rx + 22, ry, 178, 20).build());
                } else {
                    addDrawableChild(ButtonWidget.builder(Text.literal(s.label + ": " + s.display()), b -> {
                        s.cycle(true);
                        clearAndInit();
                    }).dimensions(rx, ry, 200, 20).build());
                }
                ry += 22;
            }
            addDrawableChild(ButtonWidget.builder(Text.literal("Restaurar padrão do módulo"), b -> {
                selected.resetDefaults();
                clearAndInit();
            }).dimensions(rx, ry + 4, 200, 20).build());
        }

        int by = height - 28;
        addDrawableChild(ButtonWidget.builder(Text.literal("Perfil: " + ProfileManager.active() + " (trocar)"), b -> {
            ProfileManager.next();
            clearAndInit();
        }).dimensions(left, by, 140, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Novo perfil"), b -> {
            ProfileManager.createFromCurrent();
            clearAndInit();
        }).dimensions(left + 144, by, 90, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Fechar"), b -> close())
            .dimensions(width - 96, by, 80, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("Kairo Client"), width / 2, 12, 0xFFFFFFFF);
        if (selected != null) {
            ctx.drawTextWithShadow(textRenderer, Text.literal(selected.description), 176, 38, 0xFFAAAAAA);
        } else {
            ctx.drawTextWithShadow(textRenderer, Text.literal("Selecione um módulo à esquerda."), 176, 38, 0xFFAAAAAA);
        }
    }

    @Override
    public void removed() {
        ProfileManager.save();
    }
}
