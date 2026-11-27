package dev.kairo.util;

import java.util.ArrayDeque;

/** Conta cliques por segundo a partir do estado (pressionado/solto) do botao. Sem dependencia do Minecraft. */
public final class CpsCounter {
    private final ArrayDeque<Long> clicks = new ArrayDeque<>();
    private boolean wasDown;

    public void update(boolean down, long nowMs) {
        if (down && !wasDown) clicks.addLast(nowMs);
        wasDown = down;
        prune(nowMs);
    }

    public int cps(long nowMs) {
        prune(nowMs);
        return clicks.size();
    }

    private void prune(long now) {
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000) clicks.pollFirst();
    }
}
