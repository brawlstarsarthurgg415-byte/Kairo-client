package dev.kairo.modules;

import dev.kairo.core.Category;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public final class DirectionModule extends HudModule {
    private static final String[] NAMES = {"N", "NE", "L", "SE", "S", "SO", "O", "NO"};
    private static final List<String> DEMO = List.of("Direção: NE (45°)");

    public DirectionModule() {
        super("direction", "Direção", "Mostra para onde você está olhando (N, S, L, O).", Category.HUD, false, 500, 10);
    }

    @Override
    protected List<String> compute(MinecraftClient mc) {
        if (mc.player == null) return List.of();
        float heading = ((mc.player.getYaw() + 180f) % 360f + 360f) % 360f; // 0 = norte, sentido horario
        int idx = Math.round(heading / 45f) % 8;
        return List.of("Direção: " + NAMES[idx] + " (" + Math.round(heading) + "°)");
    }

    @Override
    protected List<String> demo() { return DEMO; }
}
