package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.Setting;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

/** Armadura e item da mao com durabilidade. */
public final class ArmorModule extends HudModule {
    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND
    };
    private static ItemStack[] demoStacks;

    private final Setting durability = add(Setting.choice("dur", "Durabilidade", 0, "Número", "Porcentagem", "Oculta"));
    private final Setting hand = add(Setting.bool("hand", "Mostrar item da mão", true));

    public ArmorModule() {
        super("armor", "Armor HUD", "Armadura e item da mão com durabilidade.", Category.PVP, true, 1000, 600);
    }

    private static ItemStack[] demoStacks() {
        if (demoStacks == null) {
            demoStacks = new ItemStack[] {
                new ItemStack(Items.NETHERITE_HELMET), new ItemStack(Items.DIAMOND_CHESTPLATE),
                new ItemStack(Items.DIAMOND_LEGGINGS), new ItemStack(Items.IRON_BOOTS), new ItemStack(Items.DIAMOND_SWORD)
            };
            demoStacks[0].setDamage(120);
            demoStacks[1].setDamage(300);
            demoStacks[2].setDamage(90);
            demoStacks[3].setDamage(60);
            demoStacks[4].setDamage(210);
        }
        return demoStacks;
    }

    private int slotCount() { return hand.bool() ? SLOTS.length : SLOTS.length - 1; }

    private ItemStack stack(MinecraftClient mc, Mode mode, int i) {
        if (mode == Mode.DEMO) return demoStacks()[i];
        return mc.player == null ? ItemStack.EMPTY : mc.player.getEquippedStack(SLOTS[i]);
    }

    private String durText(ItemStack st) {
        if (durability.get() == 2 || !st.isDamageable()) return "";
        int left = st.getMaxDamage() - st.getDamage();
        return durability.get() == 0 ? String.valueOf(left) : (left * 100 / st.getMaxDamage()) + "%";
    }

    @Override
    protected List<String> compute(MinecraftClient mc) { return List.of(); }

    @Override
    protected List<String> demo() { return List.of(); }

    @Override
    public int width(MinecraftClient mc, Mode mode) {
        int rows = 0;
        int textW = 0;
        for (int i = 0; i < slotCount(); i++) {
            ItemStack st = stack(mc, mode, i);
            if (st.isEmpty()) continue;
            rows++;
            textW = Math.max(textW, textWidth(mc.textRenderer, durText(st)));
        }
        return rows == 0 ? 0 : 4 + 16 + (textW > 0 ? 4 + textW : 0) + 4;
    }

    @Override
    public int height(MinecraftClient mc, Mode mode) {
        int rows = 0;
        for (int i = 0; i < slotCount(); i++) if (!stack(mc, mode, i).isEmpty()) rows++;
        return rows == 0 ? 0 : rows * 18 + 4;
    }

    @Override
    public void draw(DrawContext ctx, MinecraftClient mc, Mode mode) {
        int w = width(mc, mode);
        int h = height(mc, mode);
        if (w <= 0) return;
        drawPanel(ctx, 0, 0, w, h);
        TextRenderer tr = mc.textRenderer;
        int y = 2;
        for (int i = 0; i < slotCount(); i++) {
            ItemStack st = stack(mc, mode, i);
            if (st.isEmpty()) continue;
            ctx.drawItem(st, 4, y);
            String t = durText(st);
            if (!t.isEmpty()) drawLabel(ctx, tr, t, 24, y + 4, 0xFF000000 | color.get());
            y += 18;
        }
    }
}
