package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.Setting;
import dev.kairo.ui.Gfx;
import dev.kairo.util.CpsCounter;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.GameOptions;

/**
 * W A S D, espaco, LMB e RMB. O mesmo desenho serve o jogo (entradas reais) e a previa
 * (entradas simuladas pelo toque, guardadas separadamente em PREVIEW_*).
 */
public final class KeystrokesModule extends HudModule {
    public static final int W = 0, A = 1, S = 2, D = 3, SPACE = 4, LMB = 5, RMB = 6, KEYS = 7;
    private static final String[] LABELS = {"W", "A", "S", "D", "—", "LMB", "RMB"};

    // Estado da previa interativa (nunca confundido com o input real)
    private static final boolean[] PREVIEW_DOWN = new boolean[KEYS];
    private static final CpsCounter PREVIEW_L = new CpsCounter();
    private static final CpsCounter PREVIEW_R = new CpsCounter();
    // Animacao de transicao [modo][tecla]
    private final float[][] anim = new float[2][KEYS];

    private final Setting shape = add(Setting.choice("shape", "Formato", 1, "Quadrado", "Arredondado", "Circular"));
    private final Setting size = add(Setting.integer("size", "Tamanho da tecla", 22, 14, 40, 2));
    private final Setting gap = add(Setting.integer("gap", "Espaçamento", 2, 0, 8, 1));
    private final Setting keyColor = add(Setting.color("keycolor", "Cor normal", 0x000000));
    private final Setting keyAlpha = add(Setting.integer("keyalpha", "Opacidade normal (%)", 45, 0, 100, 5));
    private final Setting pressColor = add(Setting.color("presscolor", "Cor pressionada", 0xFFFFFF));
    private final Setting pressAlpha = add(Setting.integer("pressalpha", "Opacidade pressionada (%)", 85, 0, 100, 5));
    private final Setting pressText = add(Setting.color("presstext", "Texto pressionado", 0x000000));
    private final Setting border = add(Setting.bool("border", "Borda", false));
    private final Setting borderColor = add(Setting.color("bordercolor", "Cor da borda", 0xFFFFFF));
    private final Setting showSpace = add(Setting.bool("space", "Mostrar espaço", true));
    private final Setting showMouse = add(Setting.bool("mouse", "Mostrar mouse (LMB/RMB)", true));
    private final Setting showCps = add(Setting.bool("cps", "Mostrar CPS nos botões", true));

    public KeystrokesModule() {
        super("keystrokes", "Keystrokes", "Teclas WASD, espaço e mouse com CPS.", Category.PVP, true, 10, 700, false);
    }

    // ---------- previa interativa ----------
    public static void previewSet(int key, boolean down) {
        if (key < 0 || key >= KEYS) return;
        PREVIEW_DOWN[key] = down;
        long now = System.currentTimeMillis();
        if (key == LMB) PREVIEW_L.update(down, now);
        if (key == RMB) PREVIEW_R.update(down, now);
    }

    public static void previewReleaseAll() {
        for (int i = 0; i < KEYS; i++) previewSet(i, false);
    }

    // ---------- layout (coordenadas locais, sem escala) ----------
    private int visibleRows() { return 2 + (showSpace.bool() ? 1 : 0) + (showMouse.bool() ? 1 : 0); }

    private int spaceH() { return Math.max(10, size.get() * 6 / 10); }

    private int mouseH() { return showCps.bool() ? Math.max(size.get(), 22) : size.get(); }

    private int keyW() { return size.get(); }

    private int totalW() { return keyW() * 3 + gap.get() * 2; }

    @Override
    public int width(MinecraftClient mc, Mode mode) { return totalW(); }

    @Override
    public int height(MinecraftClient mc, Mode mode) {
        int h = size.get() * 2 + gap.get();
        if (showSpace.bool()) h += gap.get() + spaceH();
        if (showMouse.bool()) h += gap.get() + mouseH();
        return h;
    }

    /** Preenche out = {x, y, w, h} da tecla. Retorna false se ela nao estiver visivel. */
    public boolean keyRect(int key, int[] out) {
        int k = size.get(), g = gap.get(), tw = totalW();
        switch (key) {
            case W -> set(out, k + g, 0, k, k);
            case A -> set(out, 0, k + g, k, k);
            case S -> set(out, k + g, k + g, k, k);
            case D -> set(out, 2 * (k + g), k + g, k, k);
            case SPACE -> {
                if (!showSpace.bool()) return false;
                set(out, 0, 2 * (k + g), tw, spaceH());
            }
            case LMB, RMB -> {
                if (!showMouse.bool()) return false;
                int y = 2 * (k + g) + (showSpace.bool() ? spaceH() + g : 0);
                int hw = (tw - g) / 2;
                set(out, key == LMB ? 0 : hw + g, y, key == LMB ? hw : tw - hw - g, mouseH());
            }
            default -> { return false; }
        }
        return true;
    }

    private static void set(int[] o, int x, int y, int w, int h) {
        o[0] = x; o[1] = y; o[2] = w; o[3] = h;
    }

    /** Tecla sob o ponto local (lx, ly) ou -1. Usado pela previa para tocar nas teclas. */
    public int keyAt(float lx, float ly) {
        int[] r = new int[4];
        for (int i = 0; i < KEYS; i++) {
            if (keyRect(i, r) && lx >= r[0] && lx < r[0] + r[2] && ly >= r[1] && ly < r[1] + r[3]) return i;
        }
        return -1;
    }

    // ---------- estado ----------
    private boolean isDown(MinecraftClient mc, Mode mode, int key) {
        if (mode == Mode.DEMO) return PREVIEW_DOWN[key];
        GameOptions o = mc.options;
        return switch (key) {
            case W -> o.forwardKey.isPressed();
            case A -> o.leftKey.isPressed();
            case S -> o.backKey.isPressed();
            case D -> o.rightKey.isPressed();
            case SPACE -> o.jumpKey.isPressed();
            case LMB -> o.attackKey.isPressed();
            case RMB -> o.useKey.isPressed();
            default -> false;
        };
    }

    private int cps(Mode mode, int key) {
        long now = System.currentTimeMillis();
        if (mode == Mode.DEMO) return (key == LMB ? PREVIEW_L : PREVIEW_R).cps(now);
        return (key == LMB ? dev.kairo.util.InputTracker.LEFT : dev.kairo.util.InputTracker.RIGHT).cps(now);
    }

    @Override
    protected List<String> compute(MinecraftClient mc) { return List.of(); }

    @Override
    protected List<String> demo() { return List.of(); }

    @Override
    public void draw(DrawContext ctx, MinecraftClient mc, Mode mode) {
        TextRenderer tr = mc.textRenderer;
        float[] a = anim[mode == Mode.DEMO ? 1 : 0];
        int[] r = new int[4];
        for (int i = 0; i < KEYS; i++) {
            if (!keyRect(i, r)) continue;
            boolean down = isDown(mc, mode, i);
            a[i] += ((down ? 1f : 0f) - a[i]) * 0.45f;
            if (Math.abs((down ? 1f : 0f) - a[i]) < 0.02f) a[i] = down ? 1f : 0f;
            drawKey(ctx, tr, i, r[0], r[1], r[2], r[3], a[i], mode);
        }
    }

    private void drawKey(DrawContext ctx, TextRenderer tr, int key, int x, int y, int w, int h, float t, Mode mode) {
        int normal = (keyAlpha.get() * 255 / 100 << 24) | keyColor.get();
        int pressed = (pressAlpha.get() * 255 / 100 << 24) | pressColor.get();
        int fill = Gfx.mix(normal, pressed, t);
        int r = switch (shape.get()) {
            case 0 -> 0;
            case 1 -> radius.get();
            default -> Math.min(w, h) / 2;
        };
        Gfx.roundRect(ctx, x, y, w, h, r, fill);
        if (border.bool()) Gfx.roundOutline(ctx, x, y, w, h, r, 0xFF000000 | borderColor.get());
        int text = Gfx.mix(0xFF000000 | color.get(), 0xFF000000 | pressText.get(), t);
        String label = LABELS[key];
        boolean mouse = key == LMB || key == RMB;
        if (mouse && showCps.bool()) {
            String sub = cps(mode, key) + " CPS";
            drawLabel(ctx, tr, label, x + (w - textWidth(tr, label)) / 2, y + (h - 17) / 2, text);
            drawLabel(ctx, tr, sub, x + (w - textWidth(tr, sub)) / 2, y + (h - 17) / 2 + 9, text);
        } else {
            drawLabel(ctx, tr, label, x + (w - textWidth(tr, label)) / 2, y + (h - 8) / 2, text);
        }
    }
}
