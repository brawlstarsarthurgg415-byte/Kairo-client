package dev.kairo.ui;

import dev.kairo.core.KModule;
import dev.kairo.modules.CrosshairModule;
import dev.kairo.modules.HudModule;
import dev.kairo.modules.KeystrokesModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/**
 * Previa do modulo sobre uma cena de exemplo. Usa o MESMO codigo de desenho do jogo (modo DEMO).
 * No Keystrokes as teclas respondem ao toque, sem tocar no input real do jogador.
 */
public final class ModulePreview {
    public int x, y, w, h;
    private float lastX, lastY, lastS = 1f;
    private int heldKey = -1;

    public void bounds(int x, int y, int w, int h) {
        this.x = x; this.y = y; this.w = w; this.h = h;
    }

    public void render(DrawContext c, MinecraftClient mc, KModule m) {
        Gfx.roundRect(c, x, y, w, h, 5, Theme.SURFACE);
        c.enableScissor(x + 1, y + 1, x + w - 1, y + h - 1);
        scene(c);
        if (m instanceof HudModule hm) {
            int w0 = hm.width(mc, HudModule.Mode.DEMO);
            int h0 = hm.height(mc, HudModule.Mode.DEMO);
            if (w0 > 0 && h0 > 0) {
                float fit = Math.min((w - 16) / (float) w0, (h - 16) / (float) h0);
                float s = Math.min(hm.scalePct() / 100f, fit);
                lastS = s;
                lastX = x + (w - w0 * s) / 2f;
                lastY = y + (h - h0 * s) / 2f;
                hm.renderAt(c, mc, lastX, lastY, s, HudModule.Mode.DEMO);
            }
        } else if (m instanceof CrosshairModule cm) {
            cm.drawAt(c, x + w / 2, y + h / 2);
        } else {
            String t = "Sem prévia visual";
            Gfx.centered(c, mc.textRenderer, t, x + w / 2, y + h / 2 - 4, 1f, Theme.MUTED);
        }
        c.disableScissor();
        Gfx.roundOutline(c, x, y, w, h, 5, Theme.BORDER);
    }

    /** Cena simples (ceu, montanha, grama) para dar contexto a previa. */
    private void scene(DrawContext c) {
        int ground = y + h * 62 / 100;
        c.fillGradient(x, y, x + w, ground, 0xFF7FB3F5, 0xFFBFDDFB);
        c.fill(x, ground, x + w, y + h, 0xFF5E9A3C);
        c.fill(x, ground, x + w, ground + 3, 0xFF74B84A);
        c.fill(x, ground + 3, x + w, y + h, 0xFF6B4A2D);
        c.fill(x, ground + 3, x + w, ground + 6, 0xFF5E9A3C);
        for (int i = 0; i < 6; i++) {
            int bx = x + (i * 53 + 17) % Math.max(1, w - 16);
            c.fill(bx, ground - 8 - (i % 3) * 6, bx + 14, ground, 0xFF4F8A34);
        }
    }

    public boolean mouseDown(KModule m, double mx, double my) {
        if (!(m instanceof KeystrokesModule km) || !Gfx.in(mx, my, x, y, w, h)) return false;
        int k = km.keyAt((float) ((mx - lastX) / lastS), (float) ((my - lastY) / lastS));
        if (k < 0) return false;
        heldKey = k;
        KeystrokesModule.previewSet(k, true);
        return true;
    }

    public boolean mouseUp() {
        if (heldKey < 0) return false;
        KeystrokesModule.previewReleaseAll();
        heldKey = -1;
        return true;
    }

    public boolean holding() { return heldKey >= 0; }
}
