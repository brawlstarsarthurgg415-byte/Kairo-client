package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.Setting;
import dev.kairo.util.CpsCounter;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Conta cliques lendo o estado do mouse a cada frame (somente informativo). */
public final class CpsModule extends HudModule {
    private final CpsCounter left = new CpsCounter();
    private final CpsCounter right = new CpsCounter();
    private final Setting mode = add(Setting.choice("mode", "Mostrar", 1, "Só esquerdo", "Esquerdo | Direito"));

    public CpsModule() {
        super("cps", "CPS", "Contador de cliques por segundo (esquerdo e direito).", Category.PVP, true, 1, 22);
    }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        long now = System.currentTimeMillis();
        long handle = mc.getWindow().getHandle();
        boolean inGame = mc.currentScreen == null;
        left.update(inGame && GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS, now);
        right.update(inGame && GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS, now);
        if (mode.get() == 0) return List.of("CPS: " + left.cps(now));
        return List.of("CPS: " + left.cps(now) + " | " + right.cps(now));
    }
}
