package de.rose53.pi.weatherpi.utils;

public class Utils {

    private static long SHIFTED_DIVISOR = (long) 0x988000; //This is the 0x0131 polynomial shifted to farthest left of three bytes

    public static void delay(long howMuch) {
        try {
            Thread.sleep(howMuch);
        } catch (InterruptedException ie) {
        }
    }

    /**
    * Give this function the 2 byte message (measurement) and the check_value byte from the HTU21D
    * If it returns 0, then the transmission was good
    * If it returns something other than 0, then the communication was corrupted
    * From: http://www.nongnu.org/avr-libc/user-manual/group__util__crc.html
    * POLYNOMIAL = 0x0131 = x^8 + x^5 + x^4 + 1 : http://en.wikipedia.org/wiki/Computation_of_cyclic_redundancy_checks
    * @param data
    * @param crc
    * @return CRC check value
    */
    static public boolean checkCrc(short data, short crc) {
        long remainder = (long) data << 8; //Pad with 8 bits because we have to add in the check value
        remainder |= crc; //Add on the check value

        long divsor = SHIFTED_DIVISOR;

        //Operate on only 16 positions of max 24. The remaining 8 are our remainder and should be zero when we're done.
        for (int i = 0; i < 16; i++) {
            //Check if there is a one in the left position
            if ((remainder & (long) 1 << (23 - i)) > 0) {
                remainder ^= divsor;
            }
            divsor >>= 1; //Rotate the divisor max 16 times so that we have 8 bits left of a remainder
        }
        return remainder == 0;
    }
}
