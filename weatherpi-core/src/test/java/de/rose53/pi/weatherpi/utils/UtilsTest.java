package de.rose53.pi.weatherpi.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void testCheckCrc() {
        assertTrue(Utils.checkCrc((short)0xDC, (short)0x79));
        assertTrue(Utils.checkCrc((short)0x683A, (short)0x7C));
        assertTrue(Utils.checkCrc((short)0x4E85, (short)0x6B));

        assertFalse(Utils.checkCrc((short)0x4E86, (short)0x6B));
    }

}
