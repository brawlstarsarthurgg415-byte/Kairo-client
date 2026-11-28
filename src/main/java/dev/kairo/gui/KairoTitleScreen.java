package dev.kairo.gui;

import dev.kairo.core.ModuleManager;
import dev.kairo.core.ProfileManager;
import dev.kairo.ui.Backdrop;
import dev.kairo.ui.Btn;
import dev.kairo.ui.Gfx;
import dev.kairo.ui.Icons;
import dev.kairo.ui.Theme;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

/**
 * Menu principal do Kairo (substitui a TitleScreen via mixin). Os botoes executam as telas reais do jogo.
 * Layout inspirado na linguagem visual das referencias: trilho de icones, saudacao, cartao hero e cartoes de acao.
 */
public class KairoTitleScreen extends Screen {
    private final List<Btn> actions = new ArrayList<>();
    private final List<Btn> rail = new ArrayList<>();
    private int railW = 34;

    public KairoTitleScreen() {
        super(Text.literal("Kairo Client"));
    }

    @Override
    protected void init() {
        actions.clear();
        rail.clear();
        Runnable single = () -> client.setScreen(new SelectWorldScreen(this));
        Runnable multi = () -> client.setScreen(client.options.skipMultiplayerWarning
            ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this));
        Runnable options = () -> client.setScreen(new OptionsScreen(this, client.options));
        Runnable kairo = () -> client.setScreen(new KairoMenuScreen(this));
        Runnable editor = () -> client.setScreen(new LayoutEditorScreen(this));
        Runnable quit = () -> client.scheduleStop();

        // trilho lateral (so icones, area de toque 24x24)
        int[] icons = {Icons.HOME, Icons.PERSON, Icons.GLOBE, Icons.GEAR, Icons.GRID};
        Runnable[] acts = {() -> { }, single, multi, options, kairo};
        for (int i = 0; i < icons.length; i++) {
            Btn b = new Btn("", acts[i]).icon(icons[i]);
            b.selected = i == 0;
            b.bounds(5, 8 + i * 28, 24, 24);
            rail.add(b);
        }
        Btn q = new Btn("", quit).icon(Icons.POWER);
        q.bounds(5, height - 32, 24, 24);
        rail.add(q);

        int x = railW + 8;
        int w = width - x - 10;
        int top = 62;
        boolean wide = w >= 300;
        int bigH = Math.max(40, Math.min(62, (height - top - 60) / 2));
        if (wide) {
            int bw = (w - 6) / 2;
            actions.add(card("Um jogador", "Mundos e criativo", Icons.PERSON, single, x, top, bw, bigH, true));
            actions.add(card("Multijogador", "Servidores e amigos", Icons.GLOBE, multi, x + bw + 6, top, w - bw - 6, bigH, false));
            int sy = top + bigH + 6, sw = (w - 12) / 3;
            actions.add(card("Opções", "", Icons.GEAR, options, x, sy, sw, 30, false));
            actions.add(card("Kairo", "", Icons.GRID, kairo, x + sw + 6, sy, sw, 30, false));
            actions.add(card("Editor de HUD", "", Icons.LAYOUT, editor, x + 2 * (sw + 6), sy, w - 2 * (sw + 6), 30, false));
        } else {
            int y = top;
            actions.add(card("Um jogador", "", Icons.PERSON, single, x, y, w, 30, true)); y += 34;
            actions.add(card("Multijogador", "", Icons.GLOBE, multi, x, y, w, 30, false)); y += 34;
            actions.add(card("Opções", "", Icons.GEAR, options, x, y, w, 30, false)); y += 34;
            actions.add(card("Kairo", "", Icons.GRID, kairo, x, y, w, 30, false));
        }
    }

    private Btn card(String title, String sub, int icon, Runnable run, int x, int y, int w, int h, boolean primary) {
        Btn b = new Btn(title, run).icon(icon);
        b.primary = primary;
        b.bounds(x, y, w, h);
        subs.put(b, sub);
        return b;
    }

    private final java.util.Map<Btn, String> subs = new java.util.IdentityHashMap<>();

    @Override
    public void render(DrawContext c, int mx, int my, float delta) {
        Backdrop.draw(c, width, height);
        var tr = textRenderer;
        // trilho
        Gfx.card(c, 2, 4, railW - 2, height - 8, Theme.SURFACE, Theme.BORDER);
        for (Btn b : rail) b.render(c, tr, mx, my);

        int x = railW + 8;
        Gfx.textScaled(c, tr, "Bem-vindo de volta, " + client.getSession().getUsername() + "!", x, 10, 1.25f, Theme.TEXT, false);
        Gfx.textScaled(c, tr, "KAIRO", x, 28, 2.2f, Theme.accent(), true);
        String info = "Perfil: " + ProfileManager.active() + "  ·  Minecraft " + dev.kairo.KairoClient.mcVersion();
        Gfx.text(c, tr, info, x + 80, 36, Theme.MUTED);

        for (Btn b : actions) {
            b.render(c, tr, mx, my);
            String sub = subs.get(b);
            if (sub != null && !sub.isEmpty() && b.h >= 40) {
                int col = b.primary ? 0xFF10151C : Theme.MUTED;
                Gfx.text(c, tr, sub, b.x + (b.w - tr.getWidth(sub)) / 2, b.y + b.h / 2 + 6, col);
            }
        }
        long active = ModuleManager.all().stream().filter(m -> !m.isCore() && m.isEnabled()).count();
        Gfx.text(c, tr, active + " módulos ativos", x, height - 14, Theme.MUTED);
        String cp = "Não afiliado à Mojang Studios";
        Gfx.text(c, tr, cp, width - 8 - tr.getWidth(cp), height - 14, Theme.MUTED);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        return Btn.downAll(actions, mx, my) | Btn.downAll(rail, mx, my);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        return Btn.upAll(actions, mx, my) || Btn.upAll(rail, mx, my);
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }
}
