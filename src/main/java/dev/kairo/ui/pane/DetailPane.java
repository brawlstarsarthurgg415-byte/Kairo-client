package dev.kairo.ui.pane;

import dev.kairo.core.KModule;
import dev.kairo.core.ModuleManager;
import dev.kairo.ui.Btn;
import dev.kairo.ui.Gfx;
import dev.kairo.ui.Icons;
import dev.kairo.ui.ModulePreview;
import dev.kairo.ui.SettingsPanel;
import dev.kairo.ui.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Detalhe de um modulo: previa em tempo real + configuracoes. */
public final class DetailPane extends Pane {
    private final KModule module;
    private final Shell shell;
    private final SettingsPanel settings;
    private final ModulePreview preview = new ModulePreview();
    private final List<Btn> buttons = new ArrayList<>();
    private final Btn toggle;
    private boolean settingsActive;

    public DetailPane(KModule module, Shell shell, Runnable back) {
        this.module = module;
        this.shell = shell;
        this.settings = new SettingsPanel(module).onChange(shell::changed);
        buttons.add(new Btn("Voltar", back).icon(Icons.BACK));
        toggle = new Btn("", () -> {
            module.toggle();
            ModuleManager.markDirty();
            shell.changed();
        });
        if (!module.isCore()) buttons.add(toggle);
        buttons.add(new Btn("Padrão", () -> {
            module.resetDefaults();
            ModuleManager.markDirty();
            shell.changed();
        }));
    }

    @Override
    protected void relayout() {
        buttons.get(0).bounds(x, y, 64, 20);
        Btn reset = buttons.get(buttons.size() - 1);
        reset.bounds(x + w - 56, y, 56, 20);
        if (!module.isCore()) toggle.bounds(x + w - 56 - 4 - 84, y, 84, 20);
        int top = y + 26;
        int availH = h - 26;
        boolean wide = w >= 360;
        if (wide) {
            int leftW = w * 52 / 100;
            settings.bounds(x, top, leftW, availH);
            int pw = w - leftW - 8;
            preview.bounds(x + leftW + 8, top, pw, Math.min(availH - 30, 130));
        } else {
            int ph = Math.max(56, Math.min(84, availH * 36 / 100));
            preview.bounds(x, top, w, ph);
            settings.bounds(x, top + ph + 6, w, availH - ph - 6);
        }
    }

    @Override
    public void render(DrawContext c, MinecraftClient mc, int mx, int my) {
        toggle.label = module.isEnabled() ? "Ativo" : "Desligado";
        toggle.primary = module.isEnabled();
        for (Btn b : buttons) b.render(c, mc.textRenderer, mx, my);
        Gfx.text(c, mc.textRenderer, module.name, x + 72, y + 6, Theme.TEXT);
        preview.render(c, mc, module);
        Gfx.text(c, mc.textRenderer, Gfx.ellipsize(mc.textRenderer, module.description, preview.w), preview.x,
            preview.y + preview.h + 5, Theme.MUTED);
        if (settings.contentHeight() == 0) {
            Gfx.text(c, mc.textRenderer, "Este módulo não tem opções.", settings.x + 4, settings.y + 6, Theme.MUTED);
        }
        settings.render(c, mc.textRenderer, mx, my);
    }

    @Override
    public boolean down(double mx, double my) {
        if (Btn.downAll(buttons, mx, my)) return true;
        if (preview.mouseDown(module, mx, my)) return true;
        settingsActive = settings.mouseDown(mx, my);
        return settingsActive;
    }

    @Override
    public boolean drag(double mx, double my) {
        return settingsActive && settings.mouseDrag(mx, my);
    }

    @Override
    public boolean up(double mx, double my) {
        boolean used = false;
        if (preview.mouseUp()) used = true;
        if (settingsActive) {
            settings.mouseUp(mx, my);
            settingsActive = false;
            used = true;
        }
        return Btn.upAll(buttons, mx, my) || used;
    }

    @Override
    public boolean scroll(double mx, double my, double amount) {
        return settings.mouseScroll(mx, my, amount);
    }
}
