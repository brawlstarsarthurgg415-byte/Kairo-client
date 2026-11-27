package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.Setting;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public final class ArmorModule extends HudModule {
    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND
    };
    private final Setting durability = add(Setting.choice("dur", "Durabilidade", 0, "Número", "Porcentagem", "Oculta"));
    private final Setting hand = add(Setting.bool("hand", "Mostrar item da mão", true));

    public ArmorModule() {
        super("armor", "Armor HUD", "Armadura e item da mão com durabilidade.", Category.PVP, true, 99, 70);
    }

    @Override
    protected List<String> lines(MinecraftClient mc) { return List.of(); }

    @Override
    public void onRender(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null) return;
        int count = hand.bool() ? SLOTS.length : SLOTS.length - 1;
        int rowH = 18;
        int w = 64;
        int h = count * rowH + 2;
        int[] p = place(ctx, w, h);
        fillBg(ctx, p[0], p[1], w, h);
        int y = p[1] + 1;
        for (int i = 0; i < count; i++) {
            ItemStack st = mc.player.getEquippedStack(SLOTS[i]);
            if (!st.isEmpty()) {
                ctx.drawItem(st, p[0] + 1, y);
                if (st.isDamageable() && durability.get() != 2) {
                    int left = st.getMaxDamage() - st.getDamage();
                    String t = durability.get() == 0 ? String.valueOf(left) : (left * 100 / st.getMaxDamage()) + "%";
                    ctx.drawText(mc.textRenderer, t, p[0] + 20, y + 4, textColor(), true);
                }
            }
            y += rowH;
        }
    }
}
