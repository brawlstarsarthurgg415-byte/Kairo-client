package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.Setting;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public final class ClockModule extends HudModule {
    private static final DateTimeFormatter H24 = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter H12 = DateTimeFormatter.ofPattern("hh:mm a");
    private static final List<String> DEMO = List.of("21:45");

    private final Setting source = add(Setting.choice("source", "Hora", 0, "Real", "Do jogo"));
    private final Setting h24 = add(Setting.bool("h24", "Formato 24 horas", true));

    public ClockModule() {
        super("clock", "Relógio", "Hora real do aparelho ou hora do mundo do jogo.", Category.HUD, false, 900, 10);
    }

    @Override
    protected List<String> compute(MinecraftClient mc) {
        if (source.get() == 0) {
            return List.of(LocalTime.now().format(h24.bool() ? H24 : H12));
        }
        if (mc.world == null) return List.of();
        long t = mc.world.getTimeOfDay() % 24000L;
        int hour = (int) ((t / 1000L + 6L) % 24L);
        int min = (int) ((t % 1000L) * 60L / 1000L);
        if (h24.bool()) return List.of(String.format("%02d:%02d", hour, min));
        int h12 = hour % 12 == 0 ? 12 : hour % 12;
        return List.of(String.format("%02d:%02d %s", h12, min, hour < 12 ? "AM" : "PM"));
    }

    @Override
    protected List<String> demo() { return DEMO; }
}
