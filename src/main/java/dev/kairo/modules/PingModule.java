package dev.kairo.modules;

import dev.kairo.core.Category;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

public final class PingModule extends HudModule {
    private static final List<String> DEMO = List.of("Ping: 38 ms");

    public PingModule() {
        super("ping", "Ping", "Mostra a latência até o servidor.", Category.HUD, false, 10, 80);
    }

    @Override
    protected List<String> compute(MinecraftClient mc) {
        if (mc.player == null || mc.getNetworkHandler() == null) return List.of();
        PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return List.of("Ping: " + (e == null ? "--" : e.getLatency() + " ms"));
    }

    @Override
    protected List<String> demo() { return DEMO; }
}
