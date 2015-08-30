package de.rose53.pi.weatherpi.componets;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import de.rose53.pi.weatherpi.Display;
import de.rose53.pi.weatherpi.display.EBase;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;
import de.rose53.pi.weatherpi.events.SensorEvent.ESensorPlace;
import de.rose53.pi.weatherpi.utils.IntegerConfiguration;


@ApplicationScoped
public class BMP085 implements Displayable {

    public final static int BMP085_I2C_ADDRESS = 0x77;

    public final static int EEPROM_DATA_SIZE = 22;

    public final static int BMP085_CAL_AC1           = 0xAA;  // R   Calibration data (16 bits)
    public final static int BMP085_CAL_AC2           = 0xAC;  // R   Calibration data (16 bits)
    public final static int BMP085_CAL_AC3           = 0xAE;  // R   Calibration data (16 bits)
    public final static int BMP085_CAL_AC4           = 0xB0;  // R   Calibration data (16 bits)
    public final static int BMP085_CAL_AC5           = 0xB2;  // R   Calibration data (16 bits)
    public final static int BMP085_CAL_AC6           = 0xB4;  // R   Calibration data (16 bits)
    public final static int BMP085_CAL_B1            = 0xB6;  // R   Calibration data (16 bits)
    public final static int BMP085_CAL_B2            = 0xB8;  // R   Calibration data (16 bits)
    public final static int BMP085_CAL_MB            = 0xBA;  // R   Calibration data (16 bits)
    public final static int BMP085_CAL_MC            = 0xBC;  // R   Calibration data (16 bits)
    public final static int BMP085_CAL_MD            = 0xBE;  // R   Calibration data (16 bits)

    public final static int BMP085_CONTROL = 0xF4;

    public final static int BMP085_READTEMPCMD = 0x2E;
    public final static int BMP085_TEMPDATA    = 0xF6;

    public final static int BMP085_READPRESSURECMD = 0x34;
    public final static int BMP085_PRESSUREDATA    = 0xF6;

    public final static double TEMPERATURE_ACCURACY = 2.0f;
    public final static double PRESSURE_ACCURACY    = 2.0f;


    public enum EOSRS {
        ULTRALOWPOWER(0,5),
        STANDARD(1,8),
        HIGHRES(2,14),
        ULTRAHIGHRES(3,26);

        private final int osrs;
        private final int delay;

        private EOSRS(int osrs, int delay) {
            this.osrs = osrs;
            this.delay = delay;
        }

        public int getOsrs() {
            return osrs;
        }

        public int getDelay() {
            return delay;
        }
    }

    @Inject
    Logger logger;

    @Inject
    @IntegerConfiguration(key = "i2c.bus", defaultValue = 1)
    private int i2cBusNumber;

    private I2CDevice device;

    private short ac1;
    private short ac2;
    private short ac3;
    private int   ac4;
    private int   ac5;
    private int   ac6;
    private short b1;
    private short b2;
    private short mb;
    private short mc;
    private short md;

    private int   b5;

    private EOSRS osrs = EOSRS.STANDARD;


    private float  lastTemperature = 0.0f;
    private double lastPressure = 0.0f;

    @Inject
    @IntegerConfiguration(key = "bmp085.heightAboveSeaLevel", defaultValue = 335)
    private int heightAboveSeaLevel;

    @Inject
    Event<TemperatureEvent> temperatureEvent;

    @Inject
    Event<PressureEvent> pressureEvent;

    final ScheduledExecutorService clientProcessingPool = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init()  {
        try {
            I2CBus bus = I2CFactory.getInstance(i2cBusNumber);
            device = bus.getDevice(BMP085_I2C_ADDRESS);

            Thread.sleep(500);

            byte[] eepromData = new byte[EEPROM_DATA_SIZE];

            device.read(BMP085_CAL_AC1, eepromData, 0, EEPROM_DATA_SIZE);

            DataInputStream eepromDataDIS = new DataInputStream(new ByteArrayInputStream(eepromData));

            ac1 = eepromDataDIS.readShort();
            ac2 = eepromDataDIS.readShort();
            ac3 = eepromDataDIS.readShort();

            ac4 = eepromDataDIS.readUnsignedShort();
            ac5 = eepromDataDIS.readUnsignedShort();
            ac6 = eepromDataDIS.readUnsignedShort();

            b1 = eepromDataDIS.readShort();
            b2 = eepromDataDIS.readShort();
            mb = eepromDataDIS.readShort();
            mc = eepromDataDIS.readShort();
            md = eepromDataDIS.readShort();

            logger.debug("init: read callibration data:");
            logger.debug("      ac1 = >{}<",ac1);
            logger.debug("      ac2 = >{}<",ac2);
            logger.debug("      ac3 = >{}<",ac3);
            logger.debug("      ac4 = >{}<",ac4);
            logger.debug("      ac5 = >{}<",ac5);
            logger.debug("      b1  = >{}<",b1);
            logger.debug("      b2  = >{}<",b2);
            logger.debug("      mb  = >{}<",mb);
            logger.debug("      mc  = >{}<",mc);
            logger.debug("      md  = >{}<",md);

            clientProcessingPool.scheduleAtFixedRate(new ReadDataTask(), 10, 30, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            logger.error("init:",e);
        }

    }

    @Override
    public void display(Display display) {
        try {
            display.print(readTemperature(), 1);
            display.writeDisplay();
            delay(3000);
            display.clear();
            display.print((int)readNormalizedPressure(),EBase.DEC);

        } catch (IOException e) {
            logger.error("display:",e);
        }

    }

    public synchronized float readTemperature() throws IOException {

        device.write(BMP085_CONTROL, (byte)BMP085_READTEMPCMD);
        delay(5);

        int ut = readUncompensatedTemperature();
        int x1 = ((ut - ac6) * ac5) >> 15;
        int x2 = (mc << 11) / (x1 + md);
        b5 = x1 + x2;

        float celsius = ((b5 + 8) >> 4) / 10.0f;
        logger.debug("readTemperature: temp = >{}<",celsius);
        return celsius;
    }

    public synchronized int readPressure() throws IOException {
        device.write(BMP085_CONTROL, (byte)(BMP085_READPRESSURECMD + (osrs.getOsrs() << 6)));
        delay(osrs.getDelay());
        int up = readUncompensatedPressure();
        int b6 = b5 - 4000;
        int x1 = (b2 * ((b6 * b6) >> 12)) >> 11;
        int x2 = (ac2 * b6) >> 11;
        int x3 = x1 + x2;
        int b3 = (((ac1 * 4 + x3) << osrs.getOsrs()) + 2) / 4;

        x1 = (ac3 * b6) >> 13;
        x2 = (b1 * ((b6 * b6) >> 12)) >> 16;
        x3 = ((x1 + x2) + 2) >> 2;

        int b4 = (ac4 * (x3 + 32768)) >> 15;
        int b7 = (up - b3) * (50000 >> osrs.getOsrs());

        int p = 0;
        if (b7 < 0x80000000) {
            p = (b7 * 2) / b4;
        } else {
            p = (b7 / b4) * 2;
        }

        x1 = (p >> 8) * (p >> 8);
        x1 = (x1 * 3038) >> 16;
        x2 = (-7357 * p) >> 16;

        p = p + ((x1 + x2 + 3791) >> 4);
        logger.debug("readPressure: pressure = >{}<",p);
        return (int)p;
    }

    public double readNormalizedPressure() throws IOException {
        return readPressure() /  (100  * Math.pow((1 - heightAboveSeaLevel / 44330.0), 5.255));
    }

    private int readUncompensatedTemperature() throws IOException {
        byte[] t = new byte[2];
        int r = device.read(BMP085_TEMPDATA, t, 0, 2);

        if (r != 2) {
            logger.error("readUncompensatedTemperature: 2 bytes required, got {} bytes",r);
            throw new IOException("Cannot read temperature; r = " + r);
        }

        DataInputStream utDIS = new DataInputStream(new ByteArrayInputStream(t));
        int ut = utDIS.readUnsignedShort();
        logger.debug("readUncompensatedTemperature: temp = >{}<",ut);
        return ut;
    }

    public int readUncompensatedPressure() throws IOException {

        int msb  = device.read(BMP085_PRESSUREDATA);
        int lsb  = device.read(BMP085_PRESSUREDATA + 1);
        int xlsb = device.read(BMP085_PRESSUREDATA + 2);

        int up = ((msb << 16) + (lsb << 8) + xlsb) >> (8 - osrs.getOsrs());
        logger.debug("readUncompensatedPressure: pressure = >{}<",up);
        return up;
    }



    private static void delay(long howMuch) {
        try {
            Thread.sleep(howMuch);
        } catch (InterruptedException ie) {
        }
    }

    private class ReadDataTask implements Runnable {

        @Override
        public void run() {

            try {
                float temperature = readTemperature();
                if (lastTemperature != temperature) {
                    lastTemperature = temperature;
                    temperatureEvent.fire(new TemperatureEvent(ESensorPlace.INDOOR,"BMP085", temperature,TEMPERATURE_ACCURACY));
                }
                double pressure = readNormalizedPressure();
                if (lastPressure != pressure) {
                    lastPressure = pressure;
                    pressureEvent.fire(new PressureEvent(ESensorPlace.INDOOR,"BMP085", pressure));
                }
            } catch (IOException e) {
                logger.error("run:",e);
            }
        }
    }
}
