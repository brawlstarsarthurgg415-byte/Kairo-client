package dev.kairo.modules;

import dev.kairo.core.Category;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;

public final class PotionModule extends HudModule {
    public PotionModule() {
        super("potions", "Potion HUD", "Efeitos ativos com nível e tempo restante.", Category.PVP, false, 99, 30);
    }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        if (mc.player == null) return List.of();
        List<String> out = new ArrayList<>();
        for (StatusEffectInstance e : mc.player.getStatusEffects()) {
            String name = e.getEffectType().value().getName().getString();
            String time;
            if (e.isInfinite()) {
                time = "∞";
            } else {
                int s = e.getDuration() / 20;
                time = String.format("%d:%02d", s / 60, s % 60);
            }
            out.add(name + " " + (e.getAmplifier() + 1) + " - " + time);
        }
        return out;
    }
}
