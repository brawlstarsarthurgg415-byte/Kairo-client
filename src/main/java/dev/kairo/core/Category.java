package dev.kairo.core;

public enum Category {
    PVP("PvP"), HUD("HUD"), VISUAL("Visual"), PERFORMANCE("Desempenho"), UTILITY("Utilidades");

    public final String label;

    Category(String label) {
        this.label = label;
    }
}
