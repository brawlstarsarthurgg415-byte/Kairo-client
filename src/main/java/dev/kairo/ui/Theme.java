package dev.kairo.ui;

import dev.kairo.modules.InterfaceModule;

/** Cores do Kairo. O destaque vem do modulo de interface (configuravel). */
public final class Theme {
    public static final int BG = 0xFF090B10;
    public static final int SURFACE = 0xFF12151C;
    public static final int SURFACE2 = 0xFF1A1E28;
    public static final int SURFACE3 = 0xFF232836;
    public static final int BORDER = 0xFF2A3042;
    public static final int TEXT = 0xFFEEF1F8;
    public static final int MUTED = 0xFF8791A6;
    public static final int GOOD = 0xFF4BDE80;
    public static final int DANGER = 0xFFFF4D5E;

    private Theme() {}

    public static int accent() { return 0xFF000000 | InterfaceModule.accentRgb(); }

    /** Destaque translucido (0..1). */
    public static int accentA(float a) { return Gfx.alpha(accent(), a); }

    public static boolean animations() { return InterfaceModule.animationsOn(); }
}
