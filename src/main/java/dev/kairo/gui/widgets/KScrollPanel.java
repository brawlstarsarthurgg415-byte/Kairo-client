package dev.kairo.gui.widgets;

import dev.kairo.gui.KairoTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

/**
 * Painel rolavel com suporte a toque (arrastar para rolar) e ao scroll do mouse.
 * Desenha as linhas via {@link #drawRows}; o recorte (scissor) garante que nada
 * transborde a area do painel.
 */
public abstract class KScrollPanel extends ClickableWidget {
    protected final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    protected double scroll;

    protected KScrollPanel(int x, int y, int w, int h) {
        super(x, y, w, h, Text.literal("Kairo panel"));
    }

    /** Altura total do conteudo em pixels (antes do recorte). */
    protected abstract int contentHeight();

    /** Desenha as linhas visiveis; {@code rowY} ja inclui o deslocamento do scroll. */
    protected abstract void drawRows(DrawContext ctx, int mouseX, int mouseY, float delta);

    protected int maxScroll() {
        return Math.max(0, contentHeight() - getHeight());
    }

    protected void scrollTo(double v) {
        scroll = Math.max(0, Math.min(maxScroll(), v));
    }

    @Override
    protected void renderWidget(DrawContext ctx, int mouseX, int mouseY, float delta) {
        KairoTheme.panel(ctx, getX(), getY(), getWidth(), getHeight());
        ctx.enableScissor(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1);
        ctx.getMatrices().push();
        ctx.getMatrices().translate(0, -scroll, 0);
        drawRows(ctx, mouseX, mouseY, delta);
        ctx.getMatrices().pop();
        ctx.disableScissor();
        // Indicador de posicao do scroll, discreto.
        int max = maxScroll();
        if (max > 0) {
            int trackH = getHeight() - 8;
            int thumbH = Math.max(12, trackH * getHeight() / contentHeight());
            int thumbY = getY() + 4 + (int) ((trackH - thumbH) * (scroll / max));
            ctx.fill(getX() + getWidth() - 3, thumbY, getX() + getWidth() - 2, thumbY + thumbH, 0x60FFFFFF);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!visible || !active) return false;
        if (mouseX < getX() || mouseX > getX() + getWidth() || mouseY < getY() || mouseY > getY() + getHeight()) return false;
        scrollTo(scroll - verticalAmount * 16);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible || !active) return false;
        if (mouseX < getX() || mouseX > getX() + getWidth() || mouseY < getY() || mouseY > getY() + getHeight()) return false;
        return rowClicked(mouseX, mouseY - getY() + scroll, button);
    }

    /** Clique em uma linha; {@code localY} inclui o scroll. Retorne true se consumiu. */
    protected abstract boolean rowClicked(double mouseX, double localY, int button);

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!visible || !active) return false;
        // Arrastar com toque/mouse rola o painel verticalmente.
        scrollTo(scroll - deltaY);
        return true;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getMessage());
    }
}
