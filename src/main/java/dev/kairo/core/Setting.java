package dev.kairo.core;

/** Configuracao de um modulo. Tudo e guardado como int (bool = 0/1, cor = 0xRRGGBB). */
public final class Setting {
    public enum Kind { BOOL, INT, CHOICE, COLOR }

    public static final int[] PALETTE = {
        0xFFFFFF, 0xFF5555, 0x55FF55, 0x5555FF, 0x55FFFF, 0xFFFF55, 0xFF55FF, 0xFFAA00, 0x000000,
        0x888888, 0x2E2E2E, 0xFF7A1A
    };

    public final String id;
    public final String label;
    public final Kind kind;
    public final int def;
    public final int min;
    public final int max;
    public final int step;
    public final String[] choices;
    private int value;
    private boolean hidden;

    private Setting(String id, String label, Kind kind, int def, int min, int max, int step, String[] choices) {
        this.id = id;
        this.label = label;
        this.kind = kind;
        this.def = def;
        this.min = min;
        this.max = max;
        this.step = step;
        this.choices = choices;
        this.value = def;
    }

    public static Setting bool(String id, String label, boolean def) {
        return new Setting(id, label, Kind.BOOL, def ? 1 : 0, 0, 1, 1, null);
    }

    public static Setting integer(String id, String label, int def, int min, int max, int step) {
        return new Setting(id, label, Kind.INT, def, min, max, step, null);
    }

    public static Setting choice(String id, String label, int def, String... choices) {
        return new Setting(id, label, Kind.CHOICE, def, 0, choices.length - 1, 1, choices);
    }

    public static Setting color(String id, String label, int def) {
        return new Setting(id, label, Kind.COLOR, def, 0, 0xFFFFFF, 1, null);
    }

    /** Esconde a configuracao do painel (ela continua salva no perfil). */
    public Setting hide() { hidden = true; return this; }

    public boolean isHidden() { return hidden; }

    public int get() { return value; }

    public boolean bool() { return value != 0; }

    public void set(int v) { value = Math.max(min, Math.min(max, v)); }

    public void reset() { value = def; }

    public void cycle(boolean forward) {
        switch (kind) {
            case BOOL -> value = value == 0 ? 1 : 0;
            case INT -> {
                int n = value + (forward ? step : -step);
                if (n > max) n = min;
                if (n < min) n = max;
                value = n;
            }
            case CHOICE -> value = Math.floorMod(value + (forward ? 1 : -1), choices.length);
            case COLOR -> {
                int idx = -1;
                for (int i = 0; i < PALETTE.length; i++) if (PALETTE[i] == value) idx = i;
                idx = Math.floorMod(idx + (forward ? 1 : -1), PALETTE.length);
                value = PALETTE[idx];
            }
        }
    }

    public String display() {
        return switch (kind) {
            case BOOL -> value != 0 ? "Ligado" : "Desligado";
            case INT -> String.valueOf(value);
            case CHOICE -> choices[value];
            case COLOR -> String.format("#%06X", value);
        };
    }
}
