package dev.kairo.mixin;

import dev.kairo.gui.KairoTitleScreen;
import dev.kairo.modules.InterfaceModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    /** Troca a tela inicial padrao pelo menu do Kairo (desligavel em Utilidades > Interface). */
    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen kairo$replaceTitleScreen(Screen screen) {
        if (screen != null && screen.getClass() == TitleScreen.class && InterfaceModule.customMainMenu()) {
            return new KairoTitleScreen();
        }
        return screen;
    }
}
