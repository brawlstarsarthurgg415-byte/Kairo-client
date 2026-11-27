package dev.kairo.core;

import com.google.gson.*;
import dev.kairo.KairoClient;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;

/** Perfis em JSON: .minecraft/config/kairo/profiles/NOME.json */
public final class ProfileManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path profilesDir, backupDir, configFile;
    private static String active = "default";

    private ProfileManager() {}

    public static String active() { return active; }

    public static void init() {
        try {
            Path root = FabricLoader.getInstance().getConfigDir().resolve("kairo");
            profilesDir = root.resolve("profiles");
            backupDir = root.resolve("backups");
            configFile = root.resolve("config.json");
            Files.createDirectories(profilesDir);
            Files.createDirectories(backupDir);
            if (Files.exists(configFile)) {
                JsonObject o = JsonParser.parseString(Files.readString(configFile)).getAsJsonObject();
                if (o.has("activeProfile")) active = sanitize(o.get("activeProfile").getAsString());
            }
            if (Files.exists(file(active))) {
                backup();
                load();
            } else {
                save();
            }
        } catch (Exception e) {
            KairoClient.LOGGER.error("Falha ao iniciar perfis (usando padrao em memoria)", e);
        }
    }

    private static Path file(String name) { return profilesDir.resolve(name + ".json"); }

    private static String sanitize(String s) {
        String r = s.replaceAll("[^a-zA-Z0-9_-]", "_");
        return r.isEmpty() ? "default" : r;
    }

    public static void save() {
        if (profilesDir == null) return;
        try {
            JsonObject mods = new JsonObject();
            for (KModule m : ModuleManager.all()) {
                JsonObject mo = new JsonObject();
                mo.addProperty("enabled", m.isEnabled());
                JsonObject so = new JsonObject();
                for (Setting s : m.settings()) so.addProperty(s.id, s.get());
                mo.add("settings", so);
                mods.add(m.id, mo);
            }
            JsonObject root = new JsonObject();
            root.addProperty("version", 1);
            root.add("modules", mods);
            writeAtomic(file(active), GSON.toJson(root));

            JsonObject cfg = new JsonObject();
            cfg.addProperty("activeProfile", active);
            writeAtomic(configFile, GSON.toJson(cfg));
        } catch (Exception e) {
            KairoClient.LOGGER.error("Falha ao salvar perfil {}", active, e);
        }
    }

    private static void writeAtomic(Path target, String content) throws IOException {
        Path tmp = target.resolveSibling(target.getFileName() + ".tmp");
        Files.writeString(tmp, content);
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void load() {
        try {
            JsonObject root = JsonParser.parseString(Files.readString(file(active))).getAsJsonObject();
            JsonObject mods = root.getAsJsonObject("modules");
            for (KModule m : ModuleManager.all()) {
                m.resetDefaults();
                if (mods == null || !mods.has(m.id)) continue;
                JsonObject mo = mods.getAsJsonObject(m.id);
                if (mo.has("enabled")) m.setEnabled(mo.get("enabled").getAsBoolean());
                if (mo.has("settings")) {
                    JsonObject so = mo.getAsJsonObject("settings");
                    for (Setting s : m.settings()) if (so.has(s.id)) s.set(so.get(s.id).getAsInt());
                }
            }
        } catch (Exception e) {
            KairoClient.LOGGER.error("Perfil {} invalido, usando padrao", active, e);
            ModuleManager.all().forEach(KModule::resetDefaults);
        }
    }

    private static void backup() {
        try {
            String stamp = String.valueOf(System.currentTimeMillis());
            Files.copy(file(active), backupDir.resolve(active + "-" + stamp + ".json"));
            List<Path> old = new ArrayList<>();
            try (Stream<Path> s = Files.list(backupDir)) {
                s.filter(p -> p.getFileName().toString().startsWith(active + "-")).sorted().forEach(old::add);
            }
            while (old.size() > 10) Files.deleteIfExists(old.remove(0));
        } catch (IOException e) {
            KairoClient.LOGGER.warn("Backup do perfil falhou", e);
        }
    }

    public static List<String> list() {
        List<String> names = new ArrayList<>();
        if (profilesDir == null) return names;
        try (Stream<Path> s = Files.list(profilesDir)) {
            s.map(p -> p.getFileName().toString())
                .filter(n -> n.endsWith(".json"))
                .map(n -> n.substring(0, n.length() - 5))
                .sorted()
                .forEach(names::add);
        } catch (IOException e) {
            KairoClient.LOGGER.warn("Nao foi possivel listar perfis", e);
        }
        return names;
    }

    public static void switchTo(String name) {
        save();
        active = sanitize(name);
        load();
        save();
    }

    public static void next() {
        List<String> l = list();
        if (l.isEmpty()) return;
        int i = l.indexOf(active);
        switchTo(l.get((i + 1) % l.size()));
    }

    /** Cria um perfil novo copiando o estado atual e ja ativa ele. */
    public static String createFromCurrent() {
        save();
        int n = 1;
        while (Files.exists(file("perfil-" + n))) n++;
        active = "perfil-" + n;
        save();
        return active;
    }
}
