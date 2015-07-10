package de.rose53.pi.weatherpi.pi4j.gpio.extension.ads;


import java.util.EnumSet;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.impl.PinImpl;


/**
 * This GPIO provider implements MCP3008 analog to digital converter chip as native Pi4J GPIO pins.
 * <p>
 * The MCP3008 is connected via SPI connection to the Raspberry Pi and provides
 * 8 GPIO pins that can be used for analog input pins.
 * </p>
 *
 * @author Michael Rosenauer
 *
 */
public class MCP3008Pin {

    public static final Pin INPUT_A0 = createAnalogInputPin(0, "ANALOG INPUT 0");
    public static final Pin INPUT_A1 = createAnalogInputPin(1, "ANALOG INPUT 1");
    public static final Pin INPUT_A2 = createAnalogInputPin(2, "ANALOG INPUT 2");
    public static final Pin INPUT_A3 = createAnalogInputPin(3, "ANALOG INPUT 3");
    public static final Pin INPUT_A4 = createAnalogInputPin(4, "ANALOG INPUT 4");
    public static final Pin INPUT_A5 = createAnalogInputPin(5, "ANALOG INPUT 5");
    public static final Pin INPUT_A6 = createAnalogInputPin(6, "ANALOG INPUT 6");
    public static final Pin INPUT_A7 = createAnalogInputPin(7, "ANALOG INPUT 7");


    public static Pin[] ALL = { MCP3008Pin.INPUT_A0, MCP3008Pin.INPUT_A1, MCP3008Pin.INPUT_A2, MCP3008Pin.INPUT_A3,
                                MCP3008Pin.INPUT_A4, MCP3008Pin.INPUT_A5, MCP3008Pin.INPUT_A6, MCP3008Pin.INPUT_A7};

    private static Pin createAnalogInputPin(int address, String name) {
        return new PinImpl(MCP3008GpioProvider.NAME, address, name, EnumSet.of(PinMode.ANALOG_INPUT));
    }
}
