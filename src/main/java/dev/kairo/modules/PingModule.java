package dev.kairo.modules;

import dev.kairo.core.Category;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

public final class PingModule extends HudModule {
    public PingModule() {
        super("ping", "Ping", "Mostra a latência até o servidor.", Category.HUD, false, 1, 8);
    }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        if (mc.player == null || mc.getNetworkHandler() == null) return List.of();
        PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return List.of("Ping: " + (e == null ? "--" : e.getLatency() + " ms"));
    }
}
