package dev.kairo.installer;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.regex.*;

/**
 * Kairo Client - instalador Standalone.
 * Cria uma instalacao propria no Launcher oficial do Minecraft:
 *  - perfil Fabric Loader (baixado do meta oficial do Fabric)
 *  - pasta de jogo isolada (.minecraft/kairo-client) com o mod + Fabric API
 *  - entrada "Kairo Client" no launcher_profiles.json
 */
public final class Installer {
    private static final HttpClient HTTP = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

    public static void main(String[] args) throws Exception {
        Path mc = defaultMinecraftDir();
        boolean cli = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--cli")) cli = true;
            if (args[i].equals("--dir") && i + 1 < args.length) mc = Paths.get(args[++i]);
        }
        if (cli || java.awt.GraphicsEnvironment.isHeadless()) {
            install(mc, System.out::println);
        } else {
            InstallerGui.show(mc);
        }
    }

    public static Path defaultMinecraftDir() {
        String os = System.getProperty("os.name", "").toLowerCase();
        String home = System.getProperty("user.home");
        if (os.contains("win")) {
            String appdata = System.getenv("APPDATA");
            return Paths.get(appdata != null ? appdata : home, ".minecraft");
        }
        if (os.contains("mac")) return Paths.get(home, "Library", "Application Support", "minecraft");
        return Paths.get(home, ".minecraft");
    }

    public static void install(Path mc, Consumer<String> log) throws Exception {
        Properties p = new Properties();
        try (InputStream in = Installer.class.getResourceAsStream("/kairo-installer.properties")) {
            if (in == null) throw new IOException("kairo-installer.properties ausente no instalador");
            p.load(in);
        }
        String mcVer = p.getProperty("mc_version");
        String loader = p.getProperty("loader_version");
        log.accept("Kairo Client " + p.getProperty("mod_version") + " para Minecraft " + mcVer);

        if (!Files.isDirectory(mc)) throw new IOException("Pasta do Minecraft nao encontrada: " + mc
            + " (abra o Launcher oficial uma vez antes de instalar)");

        // 1) Perfil do Fabric Loader
        log.accept("Baixando perfil do Fabric Loader " + loader + "...");
        String profileJson = httpString("https://meta.fabricmc.net/v2/versions/loader/" + mcVer + "/" + loader + "/profile/json");
        Matcher idm = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"").matcher(profileJson);
        if (!idm.find()) throw new IOException("Resposta inesperada do meta do Fabric");
        String versionId = idm.group(1);
        Path vdir = mc.resolve("versions").resolve(versionId);
        Files.createDirectories(vdir);
        Files.writeString(vdir.resolve(versionId + ".json"), profileJson, StandardCharsets.UTF_8);
        // O launcher exige um .jar (vazio) ao lado do json para perfis com inheritsFrom
        Path vjar = vdir.resolve(versionId + ".jar");
        if (!Files.exists(vjar)) Files.write(vjar, new byte[0]);
        log.accept("Perfil de versao criado: " + versionId);

        // 2) Pasta de jogo isolada + mod
        Path gameDir = mc.resolve("kairo-client");
        Path mods = gameDir.resolve("mods");
        Files.createDirectories(mods);
        try (InputStream in = Installer.class.getResourceAsStream("/kairo-mod.jar")) {
            if (in == null) throw new IOException("kairo-mod.jar ausente no instalador");
            Files.copy(in, mods.resolve("Kairo-Fabric.jar"), StandardCopyOption.REPLACE_EXISTING);
        }
        log.accept("Mod copiado para " + mods);

        // 3) Fabric API (Modrinth)
        log.accept("Procurando Fabric API para " + mcVer + "...");
        String q = "https://api.modrinth.com/v2/project/fabric-api/version?game_versions=%5B%22" + mcVer + "%22%5D&loaders=%5B%22fabric%22%5D";
        String versions = httpString(q);
        Matcher um = Pattern.compile("\"url\"\\s*:\\s*\"(https://[^\"]+\\.jar)\"").matcher(versions);
        if (!um.find()) throw new IOException("Fabric API nao encontrada para " + mcVer);
        String url = um.group(1);
        String fname = URLDecoder.decode(url.substring(url.lastIndexOf('/') + 1), StandardCharsets.UTF_8);
        Path apiJar = mods.resolve(fname);
        if (!Files.exists(apiJar)) {
            HttpResponse<InputStream> r = HTTP.send(HttpRequest.newBuilder(URI.create(url)).build(),
                HttpResponse.BodyHandlers.ofInputStream());
            try (InputStream in = r.body()) { Files.copy(in, apiJar, StandardCopyOption.REPLACE_EXISTING); }
        }
        log.accept("Fabric API: " + fname);

        // 4) Entrada no launcher_profiles.json
        addLauncherProfile(mc, versionId, gameDir, log);
        log.accept("Pronto! Abra o Minecraft Launcher e escolha a instalacao 'Kairo Client'.");
    }

    private static void addLauncherProfile(Path mc, String versionId, Path gameDir, Consumer<String> log) throws IOException {
        Path f = mc.resolve("launcher_profiles.json");
        String now = Instant.now().toString();
        String entry = "\"kairo-client\": {\"name\": \"Kairo Client\", \"type\": \"custom\", \"created\": \"" + now
            + "\", \"lastUsed\": \"" + now + "\", \"lastVersionId\": \"" + versionId
            + "\", \"gameDir\": \"" + gameDir.toString().replace("\\", "\\\\").replace("\"", "\\\"")
            + "\", \"icon\": \"Grass\"}";
        if (!Files.exists(f)) {
            Files.writeString(f, "{\"profiles\": {" + entry + "}, \"version\": 3}");
            log.accept("launcher_profiles.json criado");
            return;
        }
        String s = Files.readString(f, StandardCharsets.UTF_8);
        if (s.contains("\"kairo-client\"")) {
            log.accept("Perfil 'Kairo Client' ja existe no launcher (mantido)");
            return;
        }
        Files.copy(f, mc.resolve("launcher_profiles.json.kairo-backup"), StandardCopyOption.REPLACE_EXISTING);
        int pi = s.indexOf("\"profiles\"");
        int brace = pi < 0 ? -1 : s.indexOf('{', pi);
        if (brace < 0) throw new IOException("launcher_profiles.json em formato inesperado (backup salvo). "
            + "Crie o perfil manualmente: versao " + versionId + ", pasta de jogo " + gameDir);
        int next = brace + 1;
        while (next < s.length() && Character.isWhitespace(s.charAt(next))) next++;
        boolean empty = next < s.length() && s.charAt(next) == '}';
        String out = s.substring(0, brace + 1) + entry + (empty ? "" : ",") + s.substring(brace + 1);
        Files.writeString(f, out, StandardCharsets.UTF_8);
        log.accept("Perfil adicionado ao launcher_profiles.json (backup: launcher_profiles.json.kairo-backup)");
    }

    private static String httpString(String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).header("User-Agent", "KairoInstaller/1.0").build();
        HttpResponse<String> r = HTTP.send(req, HttpResponse.BodyHandlers.ofString());
        if (r.statusCode() != 200) throw new IOException("HTTP " + r.statusCode() + " em " + url);
        return r.body();
    }
}
