package dev.kairo.core;

import dev.kairo.KairoClient;
import dev.kairo.modules.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class ModuleManager {
    private static final List<KModule> MODULES = new ArrayList<>();

    private ModuleManager() {}

    public static void registerDefaults() {
        MODULES.clear();
        MODULES.add(new CrosshairModule());
        MODULES.add(new CpsModule());
        MODULES.add(new KeystrokesModule());
        MODULES.add(new ArmorModule());
        MODULES.add(new PotionModule());
        MODULES.add(new FpsModule());
        MODULES.add(new PingModule());
        MODULES.add(new CoordsModule());
        MODULES.add(new ToggleSprintModule());
    }

    public static List<KModule> all() { return MODULES; }

    public static List<KModule> byCategory(Category c) {
        List<KModule> out = new ArrayList<>();
        for (KModule m : MODULES) if (m.category == c) out.add(m);
        return out;
    }

    @SuppressWarnings("unchecked")
    public static <T extends KModule> T get(Class<T> type) {
        for (KModule m : MODULES) if (type.isInstance(m)) return (T) m;
        throw new IllegalStateException("Modulo nao registrado: " + type.getSimpleName());
    }

    public static void tick(MinecraftClient mc) {
        for (KModule m : MODULES) {
            if (!m.isEnabled()) continue;
            try { m.onTick(mc); } catch (Exception e) { KairoClient.LOGGER.error("Erro no tick de {}", m.id, e); }
        }
    }

    public static void render(DrawContext ctx, MinecraftClient mc) {
        for (KModule m : MODULES) {
            if (!m.isEnabled()) continue;
            try { m.onRender(ctx, mc); } catch (Exception e) { KairoClient.LOGGER.error("Erro no render de {}", m.id, e); }
        }
    }
}
