package dev.kairo;

import dev.kairo.core.ModuleManager;
import dev.kairo.core.ProfileManager;
import dev.kairo.gui.KairoScreen;
import dev.kairo.modules.CrosshairModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KairoClient implements ClientModInitializer {
    public static final String MOD_ID = "kairo";
    public static final Logger LOGGER = LoggerFactory.getLogger("Kairo");

    private static KeyBinding menuKey;
    private static KeyBinding profileKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.registerDefaults();
        ProfileManager.init();

        // Minecraft 1.21.1 ainda usa a categoria como translation key String.
        menuKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.kairo.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.kairo.main"));
        profileKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.kairo.profile", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.kairo.main"));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (menuKey.wasPressed()) mc.setScreen(new KairoScreen());
            while (profileKey.wasPressed()) {
                ProfileManager.next();
                if (mc.player != null) mc.player.sendMessage(Text.literal("Kairo: perfil " + ProfileManager.active()), true);
            }
            ModuleManager.tick(mc);
        });

        // Fabric API 0.110 / Minecraft 1.21.1 usa HudRenderCallback.
        HudRenderCallback.EVENT.register((ctx, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.options.hudHidden || mc.player == null) return;
            CrosshairModule crosshair = ModuleManager.get(CrosshairModule.class);
            if (crosshair.isEnabled()) crosshair.draw(ctx);
            ModuleManager.render(ctx, mc);
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> ProfileManager.save());
        LOGGER.info("Kairo Client iniciado (perfil: {})", ProfileManager.active());
    }
}
