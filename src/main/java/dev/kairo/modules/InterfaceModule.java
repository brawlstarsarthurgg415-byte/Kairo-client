package dev.kairo.modules;

import dev.kairo.core.Category;
import dev.kairo.core.KModule;
import dev.kairo.core.Setting;

/** Configuracoes gerais da interface do Kairo (modulo interno, sempre ativo). */
public final class InterfaceModule extends KModule {
    private static final int[] ACCENTS = {0x35D6C0, 0xFF7A1A, 0x8B6CFF, 0xFF4D5E, 0x3D8BFF, 0x4BDE80, 0xFF5FB4};
    private static InterfaceModule instance;

    private final Setting accent = add(Setting.choice("accent", "Cor de destaque", 0,
        "Kairo (água)", "Laranja", "Violeta", "Vermelho", "Azul", "Verde", "Rosa"));
    private final Setting mainMenu = add(Setting.bool("mainmenu", "Menu principal do Kairo", true));
    private final Setting animations = add(Setting.bool("animations", "Animações do menu", true));

    public InterfaceModule() {
        super("interface", "Interface", "Aparência do Kairo: cor de destaque, menu principal e animações.",
            Category.UTILITY, true);
        markCore();
        instance = this;
    }

    public static int accentRgb() {
        return instance == null ? ACCENTS[0] : ACCENTS[instance.accent.get()];
    }

    public static boolean customMainMenu() { return instance != null && instance.mainMenu.bool(); }

    public static boolean animationsOn() { return instance == null || instance.animations.bool(); }

    public static InterfaceModule get() { return instance; }
}
