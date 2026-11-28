package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.Setting;
import dev.kairo.util.InputTracker;
import java.util.List;
import net.minecraft.client.MinecraftClient;

/** Contador de CPS (somente informativo). Leitura do mouse feita em InputTracker. */
public final class CpsModule extends HudModule {
    private static final List<String> DEMO = List.of("CPS: 8 | 6");
    private final Setting mode = add(Setting.choice("mode", "Mostrar", 1, "Só esquerdo", "Esquerdo | Direito"));

    public CpsModule() {
        super("cps", "CPS", "Cliques por segundo (esquerdo e direito).", Category.PVP, true, 10, 220);
    }

    @Override
    protected List<String> compute(MinecraftClient mc) {
        long now = System.currentTimeMillis();
        if (mode.get() == 0) return List.of("CPS: " + InputTracker.LEFT.cps(now));
        return List.of("CPS: " + InputTracker.LEFT.cps(now) + " | " + InputTracker.RIGHT.cps(now));
    }

    @Override
    protected List<String> demo() { return DEMO; }
}
