package dev.kairo.modules;

import dev.kairo.core.Category;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public final class BiomeModule extends HudModule {
    private static final List<String> DEMO = List.of("Bioma: dark forest");

    public BiomeModule() {
        super("biome", "Bioma", "Mostra o bioma em que você está.", Category.HUD, false, 500, 80);
    }

    @Override
    protected List<String> compute(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return List.of();
        String name = mc.world.getBiome(mc.player.getBlockPos()).getKey()
            .map(k -> k.getValue().getPath().replace('_', ' ')).orElse("desconhecido");
        return List.of("Bioma: " + name);
    }

    @Override
    protected List<String> demo() { return DEMO; }
}
