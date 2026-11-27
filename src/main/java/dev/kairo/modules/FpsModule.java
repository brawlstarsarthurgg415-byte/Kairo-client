package dev.kairo.modules;

import dev.kairo.core.Category;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public final class FpsModule extends HudModule {
    public FpsModule() {
        super("fps", "FPS", "Mostra os quadros por segundo.", Category.HUD, true, 1, 1);
    }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        return List.of("FPS: " + mc.getCurrentFps());
    }
}
