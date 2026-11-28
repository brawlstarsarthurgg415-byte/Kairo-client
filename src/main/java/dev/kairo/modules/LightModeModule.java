package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.KModule;
import dev.kairo.core.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.ParticlesMode;

/**
 * Modo leve para celulares: reduz efeitos visuais caros usando as opcoes do proprio jogo.
 * Guarda os valores originais e os restaura ao desligar.
 */
public final class LightModeModule extends KModule {
    private final Setting particles = add(Setting.bool("particles", "Partículas mínimas", true));
    private final Setting shadows = add(Setting.bool("shadows", "Sem sombras de entidades", true));
    private final Setting clouds = add(Setting.bool("clouds", "Sem nuvens", true));

    private boolean applied;
    private ParticlesMode origParticles;
    private Boolean origShadows;
    private CloudRenderMode origClouds;

    public LightModeModule() {
        super("lightmode", "Modo leve (celular)",
            "Reduz partículas, sombras de entidades e nuvens para ganhar FPS. Restaura ao desligar.",
            Category.PERFORMANCE, false);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (!applied) {
            origParticles = mc.options.getParticles().getValue();
            origShadows = mc.options.getEntityShadows().getValue();
            origClouds = mc.options.getCloudRenderMode().getValue();
            applied = true;
        }
        if (particles.bool() && mc.options.getParticles().getValue() != ParticlesMode.MINIMAL) {
            mc.options.getParticles().setValue(ParticlesMode.MINIMAL);
        }
        if (shadows.bool() && mc.options.getEntityShadows().getValue()) {
            mc.options.getEntityShadows().setValue(false);
        }
        if (clouds.bool() && mc.options.getCloudRenderMode().getValue() != CloudRenderMode.OFF) {
            mc.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        }
    }

    @Override
    public void onDisabledTick(MinecraftClient mc) {
        restore(mc);
    }

    public void restore(MinecraftClient mc) {
        if (!applied) return;
        applied = false;
        if (origParticles != null) mc.options.getParticles().setValue(origParticles);
        if (origShadows != null) mc.options.getEntityShadows().setValue(origShadows);
        if (origClouds != null) mc.options.getCloudRenderMode().setValue(origClouds);
    }
}
