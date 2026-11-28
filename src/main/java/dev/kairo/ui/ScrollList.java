package dev.kairo.ui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * Lista rolavel pensada para toque: arrastar na vertical rola; toque curto aciona a linha;
 * arrastar na horizontal sobre um controle deslizante ajusta o valor (trava de direcao).
 */
public abstract class ScrollList {
    private enum Gesture { NONE, PENDING, SCROLL, SLIDE }

    public int x, y, w, h;
    protected float scroll;
    private Gesture gesture = Gesture.NONE;
    private double pressX, pressY, lastY;
    private int pressRow = -1;
    private int pressRowTop;

    public ScrollList bounds(int x, int y, int w, int h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
        clamp();
        return this;
    }

    protected abstract int rowCount();

    protected abstract int rowHeight(int row);

    protected abstract void renderRow(DrawContext c, TextRenderer tr, int row, int rx, int ry, int rw, int mx, int my);

    /** Linhas com controle deslizante recebem arrasto horizontal. */
    protected boolean isSlider(int row) { return false; }

    protected void onTap(int row, int localX, int localY) {}

    protected void onSlide(int row, int localX, int localY) {}

    public int contentHeight() {
        int total = 0;
        for (int i = 0; i < rowCount(); i++) total += rowHeight(i);
        return total;
    }

    protected void clamp() {
        float max = Math.max(0, contentHeight() - h);
        scroll = Math.max(0, Math.min(max, scroll));
    }

    public void render(DrawContext c, TextRenderer tr, int mx, int my) {
        clamp();
        c.enableScissor(x, y, x + w, y + h);
        int ry = y - Math.round(scroll);
        for (int i = 0; i < rowCount(); i++) {
            int rh = rowHeight(i);
            if (ry + rh >= y && ry <= y + h) renderRow(c, tr, i, x, ry, w, mx, my);
            ry += rh;
        }
        c.disableScissor();
        // barra de rolagem fina
        int total = contentHeight();
        if (total > h) {
            int barH = Math.max(14, h * h / total);
            int barY = y + Math.round((h - barH) * (scroll / (total - h)));
            c.fill(x + w - 2, barY, x + w, barY + barH, Gfx.alpha(Theme.MUTED, 0.5f));
        }
    }

    private int rowAt(double my, int[] topOut) {
        int ry = y - Math.round(scroll);
        for (int i = 0; i < rowCount(); i++) {
            int rh = rowHeight(i);
            if (my >= ry && my < ry + rh) { topOut[0] = ry; return i; }
            ry += rh;
        }
        return -1;
    }

    public boolean mouseDown(double mx, double my) {
        if (!Gfx.in(mx, my, x, y, w, h)) return false;
        int[] top = new int[1];
        pressRow = rowAt(my, top);
        pressRowTop = top[0];
        pressX = mx; pressY = my; lastY = my;
        gesture = Gesture.PENDING;
        return true;
    }

    public boolean mouseDrag(double mx, double my) {
        if (gesture == Gesture.NONE) return false;
        if (gesture == Gesture.PENDING) {
            double dx = Math.abs(mx - pressX), dy = Math.abs(my - pressY);
            if (Math.max(dx, dy) < 4) return true;
            if (pressRow >= 0 && isSlider(pressRow) && dx >= dy) gesture = Gesture.SLIDE;
            else gesture = Gesture.SCROLL;
        }
        if (gesture == Gesture.SCROLL) {
            scroll -= (float) (my - lastY);
            clamp();
        } else if (gesture == Gesture.SLIDE && pressRow >= 0) {
            onSlide(pressRow, (int) (mx - x), (int) (pressY - pressRowTop));
        }
        lastY = my;
        return true;
    }

    public boolean mouseUp(double mx, double my) {
        Gesture g = gesture;
        gesture = Gesture.NONE;
        if (g == Gesture.PENDING && pressRow >= 0) {
            int lx = (int) (mx - x);
            int ly = (int) (my - pressRowTop);
            if (isSlider(pressRow)) onSlide(pressRow, lx, ly);
            onTap(pressRow, lx, ly);
        }
        return g != Gesture.NONE;
    }

    public boolean mouseScroll(double mx, double my, double amount) {
        if (!Gfx.in(mx, my, x, y, w, h)) return false;
        scroll -= (float) (amount * 14);
        clamp();
        return true;
    }
}
