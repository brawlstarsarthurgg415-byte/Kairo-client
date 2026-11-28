package dev.kairo;

import dev.kairo.core.ModuleManager;
import dev.kairo.core.ProfileManager;
import dev.kairo.gui.KairoMenuScreen;
import dev.kairo.gui.LayoutEditorScreen;
import dev.kairo.modules.CrosshairModule;
import dev.kairo.modules.LightModeModule;
import dev.kairo.util.InputTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
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
    private static KeyBinding editorKey;
    private static KeyBinding profileKey;

    public static String version() {
        return FabricLoader.getInstance().getModContainer(MOD_ID)
            .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("dev");
    }

    public static String mcVersion() {
        return FabricLoader.getInstance().getModContainer("minecraft")
            .map(c -> c.getMetadata().getVersion().getFriendlyString()).orElse("?");
    }

    @Override
    public void onInitializeClient() {
        ModuleManager.registerDefaults();
        ProfileManager.init();

        // Minecraft 1.21.1: a categoria do atalho ainda e uma String (translation key).
        menuKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.kairo.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.kairo.main"));
        editorKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.kairo.editor", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.kairo.main"));
        profileKey = KeyBindingHelper.registerKeyBinding(
            new KeyBinding("key.kairo.profile", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.kairo.main"));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (menuKey.wasPressed()) mc.setScreen(new KairoMenuScreen(mc.currentScreen));
            while (editorKey.wasPressed()) mc.setScreen(new LayoutEditorScreen(mc.currentScreen));
            while (profileKey.wasPressed()) {
                ProfileManager.next();
                if (mc.player != null) mc.player.sendMessage(Text.literal("Kairo: perfil " + ProfileManager.active()), true);
            }
            ModuleManager.tick(mc);
        });

        // A mira vanilla e cancelada por InGameHudMixin quando a mira do Kairo esta ativa.
        HudRenderCallback.EVENT.register((ctx, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.options.hudHidden || mc.player == null) return;
            InputTracker.poll(mc);
            CrosshairModule crosshair = ModuleManager.get(CrosshairModule.class);
            if (crosshair.isEnabled() && mc.options.getPerspective().isFirstPerson()) crosshair.draw(ctx);
            ModuleManager.render(ctx, mc);
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> {
            ModuleManager.get(LightModeModule.class).restore(mc);
            ProfileManager.save();
        });
        LOGGER.info("Kairo Client {} iniciado (perfil: {})", version(), ProfileManager.active());
    }
}
