package dev.kairo.modules;

import dev.kairo.core.Category;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public final class CoordsModule extends HudModule {
    private static final List<String> DEMO = List.of("XYZ: 128.5 / 64.0 / -302.2");

    public CoordsModule() {
        super("coords", "Coordenadas", "Mostra X, Y e Z do jogador.", Category.HUD, false, 10, 150);
    }

    @Override
    protected List<String> compute(MinecraftClient mc) {
        if (mc.player == null) return List.of();
        return List.of(String.format("XYZ: %.1f / %.1f / %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()));
    }

    @Override
    protected List<String> demo() { return DEMO; }
}
