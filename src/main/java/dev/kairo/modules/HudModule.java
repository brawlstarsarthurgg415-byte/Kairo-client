package dev.kairo.modules;

import com.google.gson.JsonObject;
import dev.kairo.core.Category;
import dev.kairo.core.KModule;
import dev.kairo.core.ModuleManager;
import dev.kairo.core.Setting;
import dev.kairo.ui.Gfx;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * Elemento de HUD. O mesmo codigo de desenho serve o jogo (LIVE), as previas e os editores (DEMO).
 * Posicao em permilagem do espaco livre da tela: continua dentro da tela em qualquer resolucao.
 */
public abstract class HudModule extends KModule {
    public enum Mode { LIVE, DEMO }

    private static final Style[] STYLES = {
        null,
        Style.EMPTY.withBold(true),
        Style.EMPTY.withItalic(true),
        Style.EMPTY.withFont(Identifier.of("minecraft", "uniform"))
    };

    protected final Setting posX;
    protected final Setting posY;
    protected final Setting scale;
    protected final Setting font;
    protected final Setting color;
    protected final Setting shadow;
    protected final Setting bg;       // null quando o modulo nao usa painel de fundo
    protected final Setting bgColor;  // idem
    protected final Setting radius;
    private final Setting layer;
    private List<String> live = List.of();

    protected HudModule(String id, String name, String desc, Category cat, boolean on, int px, int py) {
        this(id, name, desc, cat, on, px, py, true);
    }

    protected HudModule(String id, String name, String desc, Category cat, boolean on, int px, int py, boolean panel) {
        super(id, name, desc, cat, on);
        posX = add(Setting.integer("px", "Posição X", px, 0, 1000, 5).hide());
        posY = add(Setting.integer("py", "Posição Y", py, 0, 1000, 5).hide());
        layer = add(Setting.integer("layer", "Camada", 10, 0, 50, 1).hide());
        scale = add(Setting.integer("scale", "Escala (%)", 100, 50, 250, 10));
        font = add(Setting.choice("font", "Fonte", 0, "Padrão", "Negrito", "Itálico", "Uniforme"));
        color = add(Setting.color("color", "Cor do texto", 0xFFFFFF));
        shadow = add(Setting.bool("shadow", "Sombra no texto", true));
        if (panel) {
            bg = add(Setting.integer("bg", "Opacidade do fundo (%)", 35, 0, 100, 5));
            bgColor = add(Setting.color("bgcolor", "Cor do fundo", 0x000000));
        } else {
            bg = null;
            bgColor = null;
        }
        radius = add(Setting.integer("radius", "Arredondamento", 3, 0, 10, 1));
    }

    // ---------- conteudo ----------
    protected abstract List<String> compute(MinecraftClient mc);

    protected abstract List<String> demo();

    @Override
    public void onTick(MinecraftClient mc) {
        live = compute(mc);
    }

    protected List<String> linesFor(Mode mode) { return mode == Mode.DEMO ? demo() : live; }

    // ---------- medidas ----------
    public int width(MinecraftClient mc, Mode mode) {
        List<String> ls = linesFor(mode);
        if (ls.isEmpty()) return 0;
        int w = 0;
        for (int i = 0; i < ls.size(); i++) w = Math.max(w, textWidth(mc.textRenderer, ls.get(i)));
        return w + 8;
    }

    public int height(MinecraftClient mc, Mode mode) {
        int n = linesFor(mode).size();
        return n == 0 ? 0 : n * 10 + 6;
    }

    // ---------- desenho (origem local 0,0) ----------
    public void draw(DrawContext ctx, MinecraftClient mc, Mode mode) {
        List<String> ls = linesFor(mode);
        int w = width(mc, mode);
        int h = height(mc, mode);
        if (w <= 0) return;
        drawPanel(ctx, 0, 0, w, h);
        for (int i = 0; i < ls.size(); i++) {
            drawLabel(ctx, mc.textRenderer, ls.get(i), 4, 4 + i * 10, 0xFF000000 | color.get());
        }
    }

    protected void drawPanel(DrawContext ctx, int x, int y, int w, int h) {
        if (bg == null) return;
        int a = bg.get() * 255 / 100;
        if (a > 0) Gfx.roundRect(ctx, x, y, w, h, radius.get(), (a << 24) | bgColor.get());
    }

    protected int textWidth(TextRenderer tr, String s) {
        Style st = STYLES[font.get()];
        return st == null ? tr.getWidth(s) : tr.getWidth(Text.literal(s).setStyle(st));
    }

    protected void drawLabel(DrawContext ctx, TextRenderer tr, String s, int x, int y, int argb) {
        Style st = STYLES[font.get()];
        if (st == null) ctx.drawText(tr, s, x, y, argb, shadow.bool());
        else ctx.drawText(tr, Text.literal(s).setStyle(st), x, y, argb, shadow.bool());
    }

    // ---------- posicionamento ----------
    public void renderPlaced(DrawContext ctx, MinecraftClient mc) {
        renderPlaced(ctx, mc, Mode.LIVE);
    }

    public void renderPlaced(DrawContext ctx, MinecraftClient mc, Mode mode) {
        int w = width(mc, mode);
        int h = height(mc, mode);
        if (w <= 0 || h <= 0) return;
        float s = scale.get() / 100f;
        renderAt(ctx, mc, placedX(ctx.getScaledWindowWidth(), w), placedY(ctx.getScaledWindowHeight(), h), s, mode);
    }

    public void renderAt(DrawContext ctx, MinecraftClient mc, float x, float y, float s, Mode mode) {
        MatrixStack m = ctx.getMatrices();
        m.push();
        m.translate(x, y, 0);
        m.scale(s, s, 1f);
        draw(ctx, mc, mode);
        m.pop();
    }

    public int placedX(int sw, int w) {
        return Math.round(Math.max(0f, sw - w * (scale.get() / 100f)) * posX.get() / 1000f);
    }

    public int placedY(int sh, int h) {
        return Math.round(Math.max(0f, sh - h * (scale.get() / 100f)) * posY.get() / 1000f);
    }

    /** Define a posicao a partir de pixels da tela (usado pelo editor ao arrastar). */
    public void placeAtPixels(int x, int y, int sw, int sh, int w, int h) {
        float s = scale.get() / 100f;
        float freeX = sw - w * s;
        float freeY = sh - h * s;
        posX.set(freeX <= 0 ? 0 : Math.round(x / freeX * 1000f));
        posY.set(freeY <= 0 ? 0 : Math.round(y / freeY * 1000f));
    }

    public void resetPosition() {
        posX.reset();
        posY.reset();
    }

    public int scalePct() { return scale.get(); }

    public void setScalePct(int v) { scale.set(v); }

    public int layer() { return layer.get(); }

    public void moveLayer(int delta) {
        layer.set(layer.get() + delta);
        ModuleManager.markDirty();
    }

    @Override
    public void migrateLegacy(JsonObject saved) {
        // Perfis da v1.0 guardavam X/Y em porcentagem (0-100) nos ids "x" e "y".
        if (saved.has("x") && !saved.has("px")) posX.set(saved.get("x").getAsInt() * 10);
        if (saved.has("y") && !saved.has("py")) posY.set(saved.get("y").getAsInt() * 10);
    }
}
