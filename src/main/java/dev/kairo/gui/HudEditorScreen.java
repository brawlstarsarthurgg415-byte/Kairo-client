package dev.kairo.gui;

import dev.kairo.core.KModule;
import dev.kairo.core.ModuleManager;
import dev.kairo.core.ProfileManager;
import dev.kairo.gui.widgets.KButton;
import dev.kairo.gui.widgets.SettingsPanel;
import dev.kairo.modules.HudModule;
import dev.kairo.util.HudLayout;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Editor visual de HUD: os elementos aparecem sobre o jogo e podem ser
 * tocados e arrastados. Posicoes sao gravadas em porcentagem da tela,
 * entao se mantem proporcionais em qualquer resolucao.
 * Funciona com toque (arrastar) e com mouse.
 */
public class HudEditorScreen extends Screen {
    private static final int PANEL_W = 148;

    private HudModule selected;
    private HudModule dragging;
    private double grabX;
    private double grabY;
    private int dragW;
    private int dragH;
    private SettingsPanel sidePanel;

    public HudEditorScreen() {
        super(Text.literal("Editor de HUD"));
    }

    @Override
    protected void init() {
        int top = 28;
        addDrawableChild(new KButton("Concluir", 8, 6, 80, 18, btn -> close()));
        addDrawableChild(new KButton("Redefinir posição", 94, 6, 110, 18, btn -> {
            if (selected != null) {
                selected.resetPosition();
                ProfileManager.save();
            }
        }));
        sidePanel = new SettingsPanel(width - PANEL_W - 6, top + 22, PANEL_W,
            height - top - 28, () -> ProfileManager.save());
        sidePanel.setModule(selected);
        addDrawableChild(sidePanel);
    }

    private int canvasRight() {
        return width - PANEL_W - 12;
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Fundo semitransparente para manter o contexto do jogo visivel.
        ctx.fill(0, 0, width, height, 0x90101418);

        // Guias centrais discretas.
        ctx.fill(width / 2, 0, width / 2 + 1, height, 0x18FFFFFF);
        ctx.fill(0, height / 2, width, height / 2 + 1, 0x18FFFFFF);

        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();

        // Conteudo real dos elementos ativos (para referencia de posicionamento).
        for (KModule m : ModuleManager.all()) {
            if (!(m instanceof HudModule hud) || !hud.isEnabled()) continue;
            try {
                hud.onRender(ctx, mc);
            } catch (Exception e) {
                // Elemento sem dados no momento; o contorno abaixo ainda e desenhado.
            }
        }

        // Contornos de selecao para todos os elementos de HUD.
        for (KModule m : ModuleManager.all()) {
            if (!(m instanceof HudModule hud)) continue;
            int[] r = hud.rect(ctx, mc);
            int color = hud == selected ? KairoTheme.accent() : 0x66FFFFFF;
            KairoTheme.border(ctx, r[0] - 1, r[1] - 1, r[2] + 2, r[3] + 2, color);
            // Rotulo acima do elemento.
            String label = hud.isEnabled() ? hud.name : hud.name + " (oculto)";
            int lw = textRenderer.getWidth(label);
            int lx = Math.max(2, Math.min(width - lw - 4, r[0]));
            int ly = Math.max(2, r[1] - 11);
            ctx.fill(lx - 2, ly - 1, lx + lw + 2, ly + 9, 0xD0101420);
            ctx.drawText(textRenderer, label, lx, ly, color, false);
        }

        super.render(ctx, mouseX, mouseY, delta);

        ctx.drawText(textRenderer, "EDITOR DE HUD — toque e arraste os elementos", 210, 11, KairoTheme.MUTED, false);
        if (selected != null) {
            ctx.drawText(textRenderer, selected.name + "  X:" + selected.xPercent() + "%  Y:" + selected.yPercent() + "%",
                width - PANEL_W - 6, 16, KairoTheme.accent(), true);
        } else {
            ctx.drawText(textRenderer, "Selecione um elemento para ajustar escala, cor e fundo.",
                width - PANEL_W - 6, 16, KairoTheme.MUTED, false);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        if (mouseX > canvasRight()) return false;
        // Teste de hit de cima para baixo (ultimo na ordem = elemento mais "alto").
        net.minecraft.client.MinecraftClient mc = net.minecraft.client.MinecraftClient.getInstance();
        int sw = mc.getWindow().getScaledWidth();
        int sh = mc.getWindow().getScaledHeight();
        HudModule hit = null;
        for (KModule m : ModuleManager.all()) {
            if (!(m instanceof HudModule hud)) continue;
            int[] r = hud.rect(sw, sh, mc);
            if (mouseX >= r[0] && mouseX <= r[0] + r[2] && mouseY >= r[1] && mouseY <= r[1] + r[3]) {
                hit = hud;
            }
        }
        if (hit != null) {
            selected = hit;
            dragging = hit;
            int[] r = hit.rect(sw, sh, mc);
            grabX = mouseX - r[0];
            grabY = mouseY - r[1];
            dragW = r[2];
            dragH = r[3];
            if (sidePanel != null) sidePanel.setModule(hit);
            return true;
        }
        selected = null;
        if (sidePanel != null) sidePanel.setModule(null);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging != null) {
            int sw = canvasRight();
            int sh = height;
            int nx = (int) (mouseX - grabX);
            int ny = (int) (mouseY - grabY);
            dragging.setPositionPercent(
                HudLayout.toPercent(nx, sw, dragW),
                HudLayout.toPercent(ny, sh, dragH));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging != null) {
            dragging = null;
            ProfileManager.save();
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selected != null) {
            int dx = 0, dy = 0;
            if (keyCode == GLFW.GLFW_KEY_LEFT) dx = -5;
            else if (keyCode == GLFW.GLFW_KEY_RIGHT) dx = 5;
            else if (keyCode == GLFW.GLFW_KEY_UP) dy = -5;
            else if (keyCode == GLFW.GLFW_KEY_DOWN) dy = 5;
            if (dx != 0 || dy != 0) {
                selected.setPositionPercent(
                    HudLayout.clampPercent(selected.xPercent() + dx),
                    HudLayout.clampPercent(selected.yPercent() + dy));
                ProfileManager.save();
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void removed() {
        ProfileManager.save();
    }
}
