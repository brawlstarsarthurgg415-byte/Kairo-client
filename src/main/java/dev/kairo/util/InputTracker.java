package dev.kairo.util;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Le o mouse uma vez por frame e alimenta os contadores de CPS (somente informativo). */
public final class InputTracker {
    public static final CpsCounter LEFT = new CpsCounter();
    public static final CpsCounter RIGHT = new CpsCounter();

    private InputTracker() {}

    public static void poll(MinecraftClient mc) {
        long now = System.currentTimeMillis();
        boolean inGame = mc.currentScreen == null;
        long h = mc.getWindow().getHandle();
        LEFT.update(inGame && GLFW.glfwGetMouseButton(h, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS, now);
        RIGHT.update(inGame && GLFW.glfwGetMouseButton(h, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS, now);
    }
}
