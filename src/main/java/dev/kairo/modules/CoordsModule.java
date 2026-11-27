package dev.kairo.modules;

import dev.kairo.core.Category;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public final class CoordsModule extends HudModule {
    public CoordsModule() {
        super("coords", "Coordenadas", "Mostra X, Y e Z do jogador.", Category.HUD, false, 1, 15);
    }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        if (mc.player == null) return List.of();
        return List.of(String.format("XYZ: %.1f / %.1f / %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()));
    }
}
