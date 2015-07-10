package de.rose53.pi.weatherpi.pi4j.gpio.extension.ads;

import java.io.IOException;
import java.util.Arrays;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.GpioProviderBase;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.event.PinAnalogValueChangeEvent;
import com.pi4j.io.gpio.event.PinListener;
import com.pi4j.wiringpi.Spi;

/**
 * @author rose
 *
 */
public class MCP3008GpioProvider extends GpioProviderBase implements GpioProvider {

	@Inject
    Logger logger;

    public static final String NAME = MCP3008GpioProvider.class.getName();
    public static final String DESCRIPTION = "MCP3008 GPIO Provider";

    // =======================================================================
    // MCP3008 VALUE RANGES
    // =======================================================================
    public static final int MCP3008_RANGE_MAX_VALUE = 1024;
    public static final int MCP3008_RANGE_MIN_VALUE = 0;

    private final static int SPEED = 1000000;

    private final int channel;

    // this value defines the sleep time between value reads by the event monitoring thread
    private int monitorInterval = 100;

    private ADCMonitor monitor;

    // this cache value is used to track last known pin values for raising event
    private double[] cachedValue = new double[MCP3008Pin.ALL.length];
    // the threshold used to determine if a significant value warrants an event to be raised
    private double[] threshold = new double[MCP3008Pin.ALL.length];

    public MCP3008GpioProvider(int channel) throws IOException {
        this.channel = channel;
        // setup SPI for communication
        int fd = Spi.wiringPiSPISetup(channel,SPEED);
        if (fd <= -1) {
            throw new IOException("wiringPiSPISetup failed");
        }
        Arrays.fill(cachedValue, 0.0);
        Arrays.fill(threshold, 10.0);

        // start monitoring thread
        monitor = new ADCMonitor();
        monitor.start();
    }

    @Override
    public void shutdown() {

        // prevent reentrant invocation
        if(isShutdown())
            return;

        // perform shutdown login in base
        super.shutdown();

        // if a monitor is running, then shut it down now
        if (monitor != null) {
            // shutdown monitoring thread
            monitor.shutdown();
            monitor = null;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    public int getMonitorInterval(){
        return monitorInterval;
    }

    public void setMonitorInterval(int monitorInterval){
        this.monitorInterval = monitorInterval;
        if(monitorInterval < 50) {
            monitorInterval = 50;
        }
    }

    public double getImmediateValue(Pin pin) {

        if (pin == null) {
            logger.warn("getImmediateValue: pin must not be null");
            return Double.NaN;
        }
        int pinByte = 0b10000000 | (pin.getAddress() << 4);
        logger.debug("getImmediateValue: pinByte = {}, {}",pinByte, bytesToBinary(new byte[] {(byte)pinByte}));

        byte packet[] = new byte[3];
        packet[0] = 1;    // Start bit
        packet[1] = (byte) pinByte;    // register byte
        packet[2] = 0b00000000;  // data byte

        logger.debug("getImmediateValue: [TX]  {}, {}",bytesToHex(packet),bytesToBinary(packet));
        Spi.wiringPiSPIDataRW(channel, packet, packet.length);
        logger.debug("getImmediateValue: [RX]  {}, {}",bytesToHex(packet),bytesToBinary(packet));

        int retVal = ((packet[1] & 0b11) << 8) | (packet[2] & 0xFF);
        logger.debug("getImmediateValue: returning {}",retVal);
        return retVal;

    }

    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for ( int j = 0; j < bytes.length; j++ ) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static String bytesToBinary( byte[] bytes ) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ ) {
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        }
        return sb.toString();
    }
    /**
     * This class/thread is used to to actively monitor for GPIO interrupts
     */
    private class ADCMonitor extends Thread {

        private boolean shuttingDown = false;

        public ADCMonitor() {
        }

        public void shutdown() {
            shuttingDown = true;
        }

        public void run() {
            try {
                while (!shuttingDown) {
                    // determine if there is a pin state difference
                    for (Pin pin : MCP3008Pin.ALL) {
                        // get actual value from ADC chip
                        double newValue = getImmediateValue(pin);

                        // check to see if the pin value exceeds the event threshold
                        if(Math.abs(cachedValue[pin.getAddress()] - newValue) > threshold[pin.getAddress()]){

                            // cache new value (both in local event comparison cache variable and pin state cache)
                            cachedValue[pin.getAddress()] = newValue;
                            getPinCache(pin).setAnalogValue(newValue);

                            // only dispatch events for analog input pins
                            if (getMode(pin) == PinMode.ANALOG_INPUT) {
                                dispatchPinChangeEvent(pin.getAddress(), newValue);
                            }
                        }
                    }
                    // ... lets take a short breather ...
                    Thread.currentThread();
                    Thread.sleep(monitorInterval);
                }
            } catch (InterruptedException e) {
                logger.warn("run: ",e);
            }
        }

        private void dispatchPinChangeEvent(int pinAddress, double value) {

            // iterate over the pin listeners map
            for (Pin pin : listeners.keySet()) {
                // dispatch this event to the listener
                // if a matching pin address is found
                if (pin.getAddress() == pinAddress && MCP3008GpioProvider.NAME.equals(pin.getProvider())) {
                    // dispatch this event to all listener handlers
                    for (PinListener listener : listeners.get(pin)) {
                        listener.handlePinEvent(new PinAnalogValueChangeEvent(this, pin, value));
                    }
                    // we do not need to check for the other pins
                    break;
                }
            }
        }
    }
}
