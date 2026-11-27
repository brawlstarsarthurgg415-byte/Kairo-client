package dev.kairo.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.kairo.KairoClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Ajustes globais de aparencia do cliente (independentes de perfil):
 * cor de destaque da interface e animacoes do menu.
 * Persistido em config/kairo/appearance.json com gravacao atomica.
 */
public final class AppearanceManager {
    /** Paleta de cores de destaque disponiveis (RGB, sem alpha). */
    public static final int[] ACCENTS = {
        0x55D6BE, // turquesa (padrao)
        0x4FC3F7, // azul
        0xFF6E6E, // vermelho
        0xA78BFA, // violeta
        0xFFB84D, // laranja
        0x7CFF6B, // verde
        0xFF6BC1, // rosa
        0xE8E8E8  // branco
    };

    private static Path file;
    private static int accent = ACCENTS[0];
    private static boolean menuAnimations = true;

    private AppearanceManager() {}

    public static void init() {
        try {
            file = FabricLoader.getInstance().getConfigDir().resolve("kairo").resolve("appearance.json");
            if (Files.exists(file)) {
                JsonObject o = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
                if (o.has("accent")) setAccent(o.get("accent").getAsInt());
                if (o.has("menuAnimations")) menuAnimations = o.get("menuAnimations").getAsBoolean();
            }
        } catch (Exception e) {
            KairoClient.LOGGER.warn("Aparencia invalida, usando padrao", e);
        }
    }

    public static void save() {
        if (file == null) return;
        try {
            Files.createDirectories(file.getParent());
            JsonObject o = new JsonObject();
            o.addProperty("accent", accent);
            o.addProperty("menuAnimations", menuAnimations);
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            Files.writeString(tmp, o.toString());
            Files.move(tmp, file, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            KairoClient.LOGGER.warn("Falha ao salvar aparencia", e);
        }
    }

    /** Cor de destaque com alpha completo (0xAARRGGBB). */
    public static int accent() { return 0xFF000000 | (accent & 0xFFFFFF); }

    /** Cor de destaque sem alpha (0xRRGGBB), para composicoes. */
    public static int accentRgb() { return accent & 0xFFFFFF; }

    public static void setAccent(int rgb) { accent = rgb & 0xFFFFFF; }

    public static boolean menuAnimations() { return menuAnimations; }

    public static void setMenuAnimations(boolean v) { menuAnimations = v; }

    /** Alterna para a proxima cor da paleta e persiste. */
    public static int cycleAccent(boolean forward) {
        int idx = -1;
        for (int i = 0; i < ACCENTS.length; i++) if (ACCENTS[i] == accentRgb()) idx = i;
        idx = Math.floorMod(idx + (forward ? 1 : -1), ACCENTS.length);
        setAccent(ACCENTS[idx]);
        save();
        return accent();
    }
}
