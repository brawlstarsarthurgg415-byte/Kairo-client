package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.Setting;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;

public final class KeystrokesModule extends HudModule {
    private final Setting mouse = add(Setting.bool("mouse", "Mostrar mouse (LMB/RMB)", true));
    private final Setting space = add(Setting.bool("space", "Mostrar espaço", true));

    public KeystrokesModule() {
        super("keystrokes", "Keystrokes", "Mostra W A S D, espaço e botões do mouse.", Category.HUD, true, 1, 55);
    }

    @Override
    protected List<String> lines(MinecraftClient mc) { return List.of(); }

    private void key(DrawContext ctx, MinecraftClient mc, int x, int y, int w, int h, String label, boolean down) {
        int base = bg.get() * 255 / 100;
        int fill = down ? (0xB0 << 24) | 0xFFFFFF : base << 24;
        if ((fill >>> 24) > 0) ctx.fill(x, y, x + w, y + h, fill);
        int c = down ? 0xFF000000 : textColor();
        ctx.drawCenteredTextWithShadow(mc.textRenderer, label, x + w / 2, y + (h - 8) / 2, c);
    }

    @Override
    public void onRender(DrawContext ctx, MinecraftClient mc) {
        GameOptions o = mc.options;
        int k = 20;
        int gap = 2;
        int w = k * 3 + gap * 2;
        int h = k * 2 + gap;
        if (space.bool()) h += k / 2 + gap;
        if (mouse.bool()) h += k + gap;
        int[] p = place(ctx, w, h);
        int x = p[0];
        int y = p[1];
        key(ctx, mc, x + k + gap, y, k, k, "W", o.forwardKey.isPressed());
        key(ctx, mc, x, y + k + gap, k, k, "A", o.leftKey.isPressed());
        key(ctx, mc, x + k + gap, y + k + gap, k, k, "S", o.backKey.isPressed());
        key(ctx, mc, x + 2 * (k + gap), y + k + gap, k, k, "D", o.rightKey.isPressed());
        int cy = y + 2 * (k + gap);
        if (space.bool()) {
            key(ctx, mc, x, cy, w, k / 2, "—", o.jumpKey.isPressed());
            cy += k / 2 + gap;
        }
        if (mouse.bool()) {
            int hw = (w - gap) / 2;
            key(ctx, mc, x, cy, hw, k, "LMB", o.attackKey.isPressed());
            key(ctx, mc, x + hw + gap, cy, hw, k, "RMB", o.useKey.isPressed());
        }
    }
}
