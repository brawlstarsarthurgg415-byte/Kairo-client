package dev.kairo.core;

import dev.kairo.KairoClient;
import dev.kairo.modules.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class ModuleManager {
    private static final List<KModule> MODULES = new ArrayList<>();
    private static final List<HudModule> HUD_ORDER = new ArrayList<>();
    private static boolean dirty = true;

    private ModuleManager() {}

    public static void registerDefaults() {
        MODULES.clear();
        MODULES.add(new InterfaceModule());
        MODULES.add(new CrosshairModule());
        MODULES.add(new CpsModule());
        MODULES.add(new KeystrokesModule());
        MODULES.add(new ArmorModule());
        MODULES.add(new PotionModule());
        MODULES.add(new FpsModule());
        MODULES.add(new PingModule());
        MODULES.add(new CoordsModule());
        MODULES.add(new DirectionModule());
        MODULES.add(new BiomeModule());
        MODULES.add(new ClockModule());
        MODULES.add(new ToggleSprintModule());
        MODULES.add(new LightModeModule());
        dirty = true;
    }

    public static List<KModule> all() { return MODULES; }

    /** Chame quando camadas/estados mudarem para reordenar o desenho do HUD. */
    public static void markDirty() { dirty = true; }

    public static List<KModule> byCategory(Category c) {
        List<KModule> out = new ArrayList<>();
        for (KModule m : MODULES) if (m.category == c && !m.isCore()) out.add(m);
        return out;
    }

    public static List<HudModule> hudModules() {
        List<HudModule> out = new ArrayList<>();
        for (KModule m : MODULES) if (m instanceof HudModule h) out.add(h);
        return out;
    }

    /** Elementos de HUD na ordem de desenho (camada menor primeiro). Lista em cache. */
    public static List<HudModule> hudOrder() {
        if (dirty) {
            HUD_ORDER.clear();
            HUD_ORDER.addAll(hudModules());
            HUD_ORDER.sort(Comparator.comparingInt(HudModule::layer));
            dirty = false;
        }
        return HUD_ORDER;
    }

    @SuppressWarnings("unchecked")
    public static <T extends KModule> T get(Class<T> type) {
        for (KModule m : MODULES) if (type.isInstance(m)) return (T) m;
        throw new IllegalStateException("Modulo nao registrado: " + type.getSimpleName());
    }

    public static void tick(MinecraftClient mc) {
        for (int i = 0; i < MODULES.size(); i++) {
            KModule m = MODULES.get(i);
            try {
                if (m.isEnabled()) m.onTick(mc);
                else m.onDisabledTick(mc);
            } catch (Exception e) { KairoClient.LOGGER.error("Erro no tick de {}", m.id, e); }
        }
    }

    public static void render(DrawContext ctx, MinecraftClient mc) {
        List<HudModule> order = hudOrder();
        for (int i = 0; i < order.size(); i++) {
            HudModule m = order.get(i);
            if (!m.isEnabled()) continue;
            try { m.renderPlaced(ctx, mc); } catch (Exception e) { KairoClient.LOGGER.error("Erro no render de {}", m.id, e); }
        }
    }
}
