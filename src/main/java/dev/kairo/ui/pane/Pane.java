package dev.kairo.ui.pane;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Area de conteudo do menu do Kairo. Recebe toque/arrasto ja em coordenadas da tela. */
public abstract class Pane {
    public int x, y, w, h;

    public final void bounds(int x, int y, int w, int h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
        relayout();
    }

    protected void relayout() {}

    public abstract void render(DrawContext c, MinecraftClient mc, int mx, int my);

    public boolean down(double mx, double my) { return false; }

    public boolean drag(double mx, double my) { return false; }

    public boolean up(double mx, double my) { return false; }

    public boolean scroll(double mx, double my, double amount) { return false; }
}
