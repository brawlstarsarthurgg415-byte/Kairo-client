package dev.kairo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CpsCounterTest {
    @Test
    void countsPressesNotHeldFrames() {
        CpsCounter c = new CpsCounter();
        long t = 0;
        for (int i = 0; i < 5; i++) {
            c.update(true, t);
            c.update(true, t + 10); // segurando nao conta de novo
            c.update(false, t + 20);
            t += 100;
        }
        assertEquals(5, c.cps(t));
    }

    @Test
    void forgetsClicksOlderThanOneSecond() {
        CpsCounter c = new CpsCounter();
        c.update(true, 0);
        c.update(false, 50);
        assertEquals(1, c.cps(900));
        assertEquals(0, c.cps(1100));
    }
}
