package de.rose53.pi.weatherpi.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class WinddirectionTest {

    @Test
    public void testN() {
        assertEquals(Winddirection.N, Winddirection.fromDegrees(0));
        assertEquals(Winddirection.N, Winddirection.fromDegrees(359));
    }

    @Test
    public void testE() {
        assertEquals(Winddirection.E, Winddirection.fromDegrees(90));
    }

    @Test
    public void testS() {
        assertEquals(Winddirection.S, Winddirection.fromDegrees(180));
    }

    @Test
    public void testW() {
        assertEquals(Winddirection.W, Winddirection.fromDegrees(270));
    }


    @Test
    public void testAll() {

        for (double d = 0.0; d <= 360.0; d += 1.0) {
            assertNotNull(Winddirection.fromDegrees(d));
        }
    }
}
