package dev.kairo.mixin;

import dev.kairo.core.ModuleManager;
import dev.kairo.modules.CrosshairModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    /** Quando a mira do Kairo esta ativa, a mira original nao e desenhada (evita duas miras). */
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void kairo$replaceCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (ModuleManager.get(CrosshairModule.class).isEnabled()) ci.cancel();
    }
}
