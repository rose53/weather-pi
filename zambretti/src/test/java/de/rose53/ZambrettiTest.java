package de.rose53;

import static org.junit.Assert.*;

import java.time.Month;

import org.junit.Test;

public class ZambrettiTest {


    @Test
    public void testNullPressureTendency() {

        Zambretti zambretti = new Zambretti();

        assertNotNull(zambretti);

        assertNull(zambretti.forecast(null, null, null));
    }

    @Test
    public void testNullPressure() {

        Zambretti zambretti = new Zambretti();

        assertNotNull(zambretti);

        assertNull(zambretti.forecast(-3.0, null, null));
    }

    @Test
    public void testNullMonth() {

        Zambretti zambretti = new Zambretti();

        assertNotNull(zambretti);

        assertNull(zambretti.forecast(-3.0, 1027.0, null));
    }

    @Test
    public void test() {
        Zambretti zambretti = new Zambretti();

        assertNotNull(zambretti);

        assertEquals("Fine Becoming Less Settled",zambretti.forecast(-3.0, 1027.0, Month.NOVEMBER));
    }

}
