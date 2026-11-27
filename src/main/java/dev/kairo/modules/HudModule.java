package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.KModule;
import dev.kairo.core.Setting;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/** Base para modulos de HUD: posicao em % da tela (funciona em qualquer resolucao). */
public abstract class HudModule extends KModule {
    protected final Setting posX;
    protected final Setting posY;
    protected final Setting bg;
    protected final Setting color;

    protected HudModule(String id, String name, String desc, Category cat, boolean on, int x, int y) {
        super(id, name, desc, cat, on);
        posX = add(Setting.integer("x", "Posição X (%)", x, 0, 100, 5));
        posY = add(Setting.integer("y", "Posição Y (%)", y, 0, 100, 5));
        bg = add(Setting.integer("bg", "Fundo (%)", 35, 0, 100, 5));
        color = add(Setting.color("color", "Cor do texto", 0xFFFFFF));
    }

    protected abstract List<String> lines(MinecraftClient mc);

    protected int[] place(DrawContext ctx, int w, int h) {
        int x = (ctx.getScaledWindowWidth() - w) * posX.get() / 100;
        int y = (ctx.getScaledWindowHeight() - h) * posY.get() / 100;
        return new int[] {x, y};
    }

    protected int textColor() { return 0xFF000000 | color.get(); }

    protected void fillBg(DrawContext ctx, int x, int y, int w, int h) {
        int a = bg.get() * 255 / 100;
        if (a > 0) ctx.fill(x, y, x + w, y + h, a << 24);
    }

    @Override
    public void onRender(DrawContext ctx, MinecraftClient mc) {
        List<String> ls = lines(mc);
        if (ls == null || ls.isEmpty()) return;
        TextRenderer tr = mc.textRenderer;
        int maxW = 0;
        for (String l : ls) maxW = Math.max(maxW, tr.getWidth(l));
        int w = maxW + 8;
        int h = ls.size() * 10 + 6;
        int[] p = place(ctx, w, h);
        fillBg(ctx, p[0], p[1], w, h);
        for (int i = 0; i < ls.size(); i++) {
            ctx.drawText(tr, ls.get(i), p[0] + 4, p[1] + 4 + i * 10, textColor(), true);
        }
    }
}
