package dev.kairo.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SettingTest {
    @Test
    void intWrapsAroundAndClamps() {
        Setting s = Setting.integer("a", "A", 9, 0, 10, 5);
        s.cycle(true);
        assertEquals(0, s.get()); // 14 > max -> volta ao minimo
        s.set(999);
        assertEquals(10, s.get());
        s.set(-5);
        assertEquals(0, s.get());
    }

    @Test
    void boolToggles() {
        Setting s = Setting.bool("b", "B", false);
        assertFalse(s.bool());
        s.cycle(true);
        assertTrue(s.bool());
    }

    @Test
    void choiceCyclesBothWays() {
        Setting s = Setting.choice("c", "C", 0, "x", "y", "z");
        s.cycle(false);
        assertEquals("z", s.display());
        s.cycle(true);
        assertEquals("x", s.display());
    }

    @Test
    void resetRestoresDefault() {
        Setting s = Setting.color("k", "K", 0x112233);
        s.cycle(true);
        s.reset();
        assertEquals(0x112233, s.get());
    }
}
