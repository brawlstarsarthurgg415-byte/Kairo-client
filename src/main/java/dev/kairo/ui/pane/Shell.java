package dev.kairo.ui.pane;

import dev.kairo.core.KModule;

/** O que os paineis podem pedir a tela principal do Kairo. */
public interface Shell {
    void openModule(KModule m);

    void openTab(int tab);

    void openLayoutEditor();

    void openLibrary();

    /** Alguma configuracao mudou (sera salva ao soltar o dedo / fechar). */
    void changed();
}
