package dev.kairo.modules;

import dev.kairo.core.Category;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public final class FpsModule extends HudModule {
    private static final List<String> DEMO = List.of("FPS: 144");

    public FpsModule() {
        super("fps", "FPS", "Mostra os quadros por segundo.", Category.HUD, true, 10, 10);
    }

    @Override
    protected List<String> compute(MinecraftClient mc) { return List.of("FPS: " + mc.getCurrentFps()); }

    @Override
    protected List<String> demo() { return DEMO; }
}
