package dev.kairo.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

/** Base de todo modulo do cliente. */
public abstract class KModule {
    public final String id;
    public final String name;
    public final String description;
    public final Category category;
    private final boolean defaultEnabled;
    private boolean enabled;
    private final List<Setting> settings = new ArrayList<>();

    protected KModule(String id, String name, String description, Category category, boolean defaultEnabled) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.defaultEnabled = defaultEnabled;
        this.enabled = defaultEnabled;
    }

    protected Setting add(Setting s) {
        settings.add(s);
        return s;
    }

    public List<Setting> settings() { return Collections.unmodifiableList(settings); }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean e) { enabled = e; }

    public void toggle() { enabled = !enabled; }

    public void resetDefaults() {
        enabled = defaultEnabled;
        settings.forEach(Setting::reset);
    }

    /** Chamado todo tick do cliente, somente se ativo. */
    public void onTick(MinecraftClient mc) {}

    /** Chamado todo frame na camada do HUD, somente se ativo. */
    public void onRender(DrawContext ctx, MinecraftClient mc) {}
}
