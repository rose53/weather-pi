package de.rose53.pi.weatherpi.display;

import static java.lang.Math.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import de.rose53.pi.weatherpi.Display;
import de.rose53.pi.weatherpi.utils.IntegerConfiguration;

@ApplicationScoped
public class LEDBackpack implements AutoCloseable, Display {

	@Inject
    Logger logger;

    @Inject
    @IntegerConfiguration(key = "i2c.bus", defaultValue = 1)
    private int i2cBusNumber;

    private I2CDevice device;

    static private final int[] numbertable = {
        0x3F, /* 0 */
        0x06, /* 1 */
        0x5B, /* 2 */
        0x4F, /* 3 */
        0x66, /* 4 */
        0x6D, /* 5 */
        0x7D, /* 6 */
        0x07, /* 7 */
        0x7F, /* 8 */
        0x6F, /* 9 */
        0x77, /* a */
        0x7C, /* b */
        0x39, /* C */
        0x5E, /* d */
        0x79, /* E */
        0x71, /* F */
    };

    static private final int SEVENSEG_DIGITS = 5;
    static private final int COLON_POSITION   = 2;

    static private final int HT16K33_I2C_ADDRESS = 0x70;

    static private final int HT16K33_BLINK_CMD = 0x80;
    static private final int HT16K33_BLINK_DISPLAYON = 0x01;

    static private final int HT16K33_CMD_BRIGHTNESS = 0x0E;

    private EDisplay    position;
    private final int[] displaybuffer = new int[SEVENSEG_DIGITS];

    public LEDBackpack()  {
        this.position = EDisplay.ONE;
    }

    @PostConstruct
    public void open()  {
    	try {
    		I2CBus bus = I2CFactory.getInstance(i2cBusNumber);
	        device = bus.getDevice(HT16K33_I2C_ADDRESS);

	        device.write((byte)0x21);  // turn on oscillator

	        blinkRate(EBlinkRate.BLINK_OFF);
	        setBrightness(15); // max brightness
		} catch (IOException e) {
			logger.error("open:",e);
		}

    }

    @PreDestroy
    @Override
    public void close() throws IOException {
        if (device != null) {
            device.write((byte)0x20);  // turn off oscillator
        }
    }

    public void setBrightness(int b) throws IOException  {
        device.write((byte)(HT16K33_CMD_BRIGHTNESS | max(min(b,15), 0)));
    }

    public void blinkRate(EBlinkRate blinkRate) throws IOException {
        if (blinkRate == null) {
            blinkRate = EBlinkRate.BLINK_OFF; // turn off if not sure
        }

        device.write((byte)(HT16K33_BLINK_CMD | HT16K33_BLINK_DISPLAYON | (blinkRate.getRate() << 1)));
    }

    int[] getDisplaybuffer() {
        return displaybuffer;
    }

    public void writeDisplay() throws IOException {
        for (int i=0; i<SEVENSEG_DIGITS; i++) {
            device.write(i << 1,(byte)(displaybuffer[i] & 0xFF));
        }
    }

    public void clear() {
        Arrays.fill(displaybuffer, 0);
    }

    public void print(char c, EBase base) {
        print((int) c, base);
    }

    public void print(int n, EBase base) {
        if (base == null) {
            write((int)n);
        } else {
            printNumber(n, base);
        }
    }

    public void print(int n) {
        print(n,EBase.DEC);
    }

    public void println() {
        position = EDisplay.ONE;
    }

    public void println(int n) {
        print(n,EBase.DEC);
        println();
    }

    public void println(double n) {
      print(n,2);
      println();
    }

    public void print(double n, int digits) {
        printFloat(n, digits,EBase.DEC);
    }

    public void print(double n) {
        print(n,2);
    }

    private int write(int c) {

      int r = 0;

      if (c == '\n' || c == '\r') {
          position = EDisplay.ONE;
      }

      if ((c >= '0') && (c <= '9')) {
    	  writeDigitNum(position, c-'0');
    	  r = 1;
      }

      position = position.getNext();

      return r;
    }

    /**
     *
     * @param display one of the four displays
     * @param bitmask the bitmask, that should be send to the display
     */
    public void writeDigitRaw(EDisplay display, int bitmask) {
        logger.debug("writeDigitRaw: display=[{}], bitmask = [0x{}]", display,Integer.toHexString(bitmask));
        if (display == null) {
            return;
        }
        displaybuffer[display.getPosition()] = bitmask;
    }

    public void drawColon(List<EColon> colonList) {
    	if (colonList == null || colonList.isEmpty()) {
    		displaybuffer[COLON_POSITION] = 0;
    		return;
    	}
    	colonList.forEach(colon -> displaybuffer[COLON_POSITION] |= colon.getBit());
    }

    public void writeDigitNum(EDisplay display, int num, boolean dot) {
        logger.debug("writeDigitNum: display=[{}], num = [{}], dot = [{}]", display,num,dot);
        if (display == null) {
            logger.warn("writeDigitNum: no display given.");
            return;
        }

        if (num < 0 || num > 15) {
            logger.error("writeDigitNum: num must be between 0 and 15.");
        }

        writeDigitRaw(display, numbertable[num]);
    }

    public void writeDigitNum(EDisplay display, int num) {
        writeDigitNum(display,num,false);
    }

    void printNumber(long n, EBase base) {
        printFloat(n, 0, base);
    }

    void printFloat(double n, int fracDigits, EBase base) {
        int numericDigits = 4; // available digits on display
        boolean isNegative = false; // true if the number is negative

        // is the number negative?
        if (n < 0) {
            isNegative = true; // need to draw sign later
            --numericDigits; // the sign will take up one digit
            n *= -1; // pretend the number is positive
        }

        // calculate the factor required to shift all fractional digits
        // into the integer part of the number
        double toIntFactor = 1.0;
        for (int i = 0; i < fracDigits; ++i)
            toIntFactor *= base.getBase();

        // create integer containing digits to display by applying
        // shifting factor and rounding adjustment
        int displayNumber = (int) Math.round(n * toIntFactor);

        // calculate upper bound on displayNumber given
        // available digits on display
        long tooBig = 1;
        for (int i = 0; i < numericDigits; ++i)
            tooBig *= base.getBase();

        // if displayNumber is too large, try fewer fractional digits
        while (displayNumber >= tooBig) {
            --fracDigits;
            toIntFactor /= base.getBase();
            displayNumber = (int) Math.round(n * toIntFactor);
        }

        // did toIntFactor shift the decimal off the display?
        if (toIntFactor < 1) {
            printError();
        } else {
            // otherwise, display the number
            drawColon(null);
            EDisplay displayPos = EDisplay.FOUR;

            if (displayNumber != 0) // if displayNumber is not 0
            {
                while (displayNumber > 0 && displayPos != null) {
                    writeDigitNum(displayPos,displayNumber % base.getBase());

                    displayNumber /= base.getBase();
                    displayPos = displayPos.getPrevious();
                }
                if (fracDigits == 1) {
                	drawColon(EColon.DECIMAL.asList());
                }
            } else {
                writeDigitNum(displayPos, 0, false);
                displayPos = displayPos.getPrevious();
            }

            // display negative sign if negative
            if (isNegative && displayPos != null) {
                writeDigitRaw(displayPos, 0x40);
                displayPos = displayPos.getPrevious();
            }

            // clear remaining display positions
            while (displayPos != null) {
                writeDigitRaw(displayPos, 0x00);
                displayPos = displayPos.getPrevious();
            }
        }
    }

    public void printError() {
        drawColon(null);

        for (EDisplay display : EDisplay.values()) {
            writeDigitRaw(display, 0x40);
        }
    }

}
