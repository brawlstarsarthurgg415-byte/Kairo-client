package dev.kairo.gui;

import dev.kairo.core.Category;
import dev.kairo.core.KModule;
import dev.kairo.core.ModuleManager;
import dev.kairo.core.ProfileManager;
import dev.kairo.core.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Kairo's responsive in-game control center, designed for desktop and narrow mobile viewports. */
public class KairoScreen extends Screen {
    private static Category category = Category.PVP;
    private static KModule selected;
    private static int settingsPage = 0;

    private static final int BG = 0xF20B1019;
    private static final int PANEL = 0xE916202D;
    private static final int PANEL_HI = 0xFF202D3C;
    private static final int ACCENT = 0xFF54D6B5;
    private static final int TEXT = 0xFFF2F6FA;
    private static final int MUTED = 0xFF9AAABD;
    private static final int ACTIVE = 0xFF244D49;

    public KairoScreen() { super(Text.literal("Kairo Client")); }

    private static String shortName(Category c) {
        return switch (c) {
            case PVP -> "PvP";
            case HUD -> "HUD";
            case VISUAL -> "Visual";
            case PERFORMANCE -> "FPS";
            case UTILITY -> "Util";
        };
    }

    private void choose(KModule module) {
        selected = module;
        settingsPage = 0;
        clearAndInit();
    }

    @Override
    protected void init() {
        int margin = 8;
        int tabY = 35;
        int tabGap = 3;
        int tabW = Math.max(42, (width - margin * 2 - tabGap * 4) / 5);
        int i = 0;
        for (Category c : Category.values()) {
            final Category target = c;
            addDrawableChild(ButtonWidget.builder(Text.literal(shortName(c)), b -> {
                category = target;
                selected = ModuleManager.byCategory(target).isEmpty() ? null : ModuleManager.byCategory(target).get(0);
                settingsPage = 0;
                clearAndInit();
            }).dimensions(margin + i * (tabW + tabGap), tabY, tabW, 20).build());
            i++;
        }

        int top = 62;
        int bottom = height - 31;
        int bodyH = Math.max(42, bottom - top);
        int leftW = Math.max(94, Math.min(154, (width - margin * 3) * 42 / 100));
        int leftX = margin;
        int rightX = leftX + leftW + margin;
        int rightW = Math.max(82, width - rightX - margin);

        int y = top + 5;
        for (KModule module : ModuleManager.byCategory(category)) {
            if (y + 21 > bottom - 2) break;
            String mark = module.isEnabled() ? "● " : "○ ";
            String label = mark + module.name;
            if (label.length() > 17) label = label.substring(0, 16) + "…";
            addDrawableChild(ButtonWidget.builder(Text.literal(label), b -> choose(module))
                .dimensions(leftX + 4, y, leftW - 8, 20).build());
            y += 23;
        }

        if (selected != null && selected.category == category) {
            int controlY = top + 5;
            addDrawableChild(ButtonWidget.builder(
                Text.literal(selected.isEnabled() ? "● ATIVO" : "○ DESATIVADO"), b -> {
                    selected.toggle();
                    clearAndInit();
                }).dimensions(rightX + 4, controlY, rightW - 8, 20).build());
            controlY += 24;

            int perPage = Math.max(1, Math.min(4, (bodyH - 62) / 22));
            int totalPages = Math.max(1, (selected.settings().size() + perPage - 1) / perPage);
            settingsPage = Math.max(0, Math.min(settingsPage, totalPages - 1));
            int from = settingsPage * perPage;
            int to = Math.min(selected.settings().size(), from + perPage);
            for (int s = from; s < to; s++) {
                Setting setting = selected.settings().get(s);
                int rowW = rightW - 8;
                if (setting.kind == Setting.Kind.INT && rowW >= 116) {
                    addDrawableChild(ButtonWidget.builder(Text.literal("−"), b -> {
                        setting.cycle(false); clearAndInit();
                    }).dimensions(rightX + 4, controlY, 20, 20).build());
                    addDrawableChild(ButtonWidget.builder(
                        Text.literal(setting.label + " " + setting.display()), b -> {
                            setting.cycle(true); clearAndInit();
                        }).dimensions(rightX + 27, controlY, Math.max(65, rowW - 23), 20).build());
                } else {
                    String label = setting.label + ": " + setting.display();
                    if (label.length() > 26) label = label.substring(0, 25) + "…";
                    addDrawableChild(ButtonWidget.builder(Text.literal(label), b -> {
                        setting.cycle(true); clearAndInit();
                    }).dimensions(rightX + 4, controlY, rowW, 20).build());
                }
                controlY += 22;
            }
            if (totalPages > 1) {
                int navY = Math.min(bottom - 23, controlY + 1);
                addDrawableChild(ButtonWidget.builder(Text.literal("‹"), b -> {
                    settingsPage = Math.max(0, settingsPage - 1); clearAndInit();
                }).dimensions(rightX + 4, navY, 22, 18).build());
                addDrawableChild(ButtonWidget.builder(Text.literal((settingsPage + 1) + "/" + totalPages), b -> {
                    settingsPage = (settingsPage + 1) % totalPages; clearAndInit();
                }).dimensions(rightX + 29, navY, Math.max(36, rightW - 58), 18).build());
                addDrawableChild(ButtonWidget.builder(Text.literal("›"), b -> {
                    settingsPage = Math.min(totalPages - 1, settingsPage + 1); clearAndInit();
                }).dimensions(rightX + rightW - 26, navY, 22, 18).build());
            }
        }

        int footerY = height - 24;
        addDrawableChild(ButtonWidget.builder(Text.literal("Perfil: " + ProfileManager.active()), b -> {
            ProfileManager.next(); clearAndInit();
        }).dimensions(margin, footerY, Math.min(112, width / 2), 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("+"), b -> {
            ProfileManager.createFromCurrent(); clearAndInit();
        }).dimensions(Math.min(124, width / 2 + 4), footerY, 20, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Fechar"), b -> close())
            .dimensions(width - 58, footerY, 50, 18).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int margin = 8;
        int top = 59;
        int bottom = height - 28;
        int leftW = Math.max(94, Math.min(154, (width - margin * 3) * 42 / 100));
        int rightX = margin + leftW + margin;

        ctx.fill(0, 0, width, height, BG);
        ctx.fill(margin, 7, width - margin, 29, PANEL_HI);
        ctx.fill(margin, top, margin + leftW, bottom, PANEL);
        ctx.fill(rightX, top, width - margin, bottom, PANEL);
        ctx.fill(margin, 30, width - margin, 32, ACCENT);
        ctx.fill(margin + 5, top, margin + leftW - 5, top + 2, ACCENT);
        ctx.fill(rightX + 5, top, width - margin - 5, top + 2, ACCENT);

        super.render(ctx, mouseX, mouseY, delta);

        ctx.drawText(textRenderer, "KAIRO", margin + 7, 14, TEXT, true);
        ctx.drawText(textRenderer, "CLIENT", margin + 48, 15, MUTED, false);
        ctx.drawText(textRenderer, shortName(category).toUpperCase() + " MODULES",
            margin + 6, top + 6, ACCENT, true);

        if (selected != null && selected.category == category) {
            ctx.drawText(textRenderer, selected.name.toUpperCase(), rightX + 7, top + 6, TEXT, true);
            String desc = selected.description;
            int maxChars = Math.max(12, (width - rightX - margin - 12) / 6);
            if (desc.length() > maxChars) desc = desc.substring(0, maxChars - 1) + "…";
            ctx.drawText(textRenderer, desc, rightX + 7, top + 18, MUTED, false);
        } else {
            ctx.drawText(textRenderer, "SELECT A MODULE", rightX + 7, top + 7, TEXT, true);
            ctx.drawText(textRenderer, "Choose a category and module.", rightX + 7, top + 20, MUTED, false);
        }
        ctx.drawText(textRenderer, "KAIRO  •  FABRIC 1.21.1", margin + 5, height - 10, MUTED, false);
    }

    @Override
    public void removed() { ProfileManager.save(); }
}
