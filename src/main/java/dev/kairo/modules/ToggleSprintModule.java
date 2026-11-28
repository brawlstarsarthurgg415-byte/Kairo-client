package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.KModule;
import net.minecraft.client.MinecraftClient;

/** Mantem o sprint ativo enquanto voce anda para frente. Usa apenas a tecla de sprint do proprio jogo. */
public final class ToggleSprintModule extends KModule {
    public ToggleSprintModule() {
        super("togglesprint", "Sprint automático", "Corre automaticamente ao andar para frente (sem agachar).",
            Category.PVP, false);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null || mc.currentScreen != null) return;
        boolean forward = mc.options.forwardKey.isPressed();
        boolean sneak = mc.options.sneakKey.isPressed();
        mc.options.sprintKey.setPressed(forward && !sneak);
    }
}
