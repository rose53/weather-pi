package de.rose53.pi.weatherpi.componets;

import static de.rose53.pi.weatherpi.utils.Utils.delay;

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
import de.rose53.pi.weatherpi.ESensorPlace;
import de.rose53.pi.weatherpi.display.EBase;
import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;
import de.rose53.pi.weatherpi.utils.IntegerConfiguration;

@ApplicationScoped
public class HTU21D implements Sensor,Displayable {

    private static final int HTDU21D_I2C_ADDRESS = 0x40;

    public final static int HTU21DF_READTEMP_HOLD = 0xE3;
    public final static int HTU21DF_READHUM_HOLD  = 0xE5;

    public final static int HTU21DF_READTEMP_NOHOLD = 0xF3;
    public final static int HTU21DF_READHUM_NOHOLD  = 0xF5;

    public final static int HTU21DF_WRITEREG = 0xE6;
    public final static int HTU21DF_READREG  = 0xE7;
    public final static int HTU21DF_RESET    = 0xFE;

    public final static double TEMPERATURE_ACCURACY = 0.3;
    //public final static double HUMIDITY_ACCURACY    = 2.0f;

    @Inject
    Logger logger;

    @Inject
    @IntegerConfiguration(key = "i2c.bus", defaultValue = 1)
    private int i2cBusNumber;

    @Inject
    Event<TemperatureEvent> temperatureEvent;

    @Inject
    Event<HumidityEvent> humidityEvent;

    private I2CDevice device;

    private double lastTemperature = 0.0;
    private double lastHumidity    = 0.0;

    final ScheduledExecutorService clientProcessingPool = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r,"HTU21DValueReader");
        t.setPriority(Thread.MIN_PRIORITY);
        return t;
    });


    @PostConstruct
    public void init()  {
        try {
            I2CBus bus = I2CFactory.getInstance(i2cBusNumber);
            device = bus.getDevice(HTDU21D_I2C_ADDRESS);

            clientProcessingPool.scheduleAtFixedRate(new ReadDataTask(), 15, 30, TimeUnit.SECONDS);
        } catch (IOException e) {
            logger.error("init:",e);
        }

    }

    @Override
    public String getName() {
        return "HTU21D";
    }

    @Override
    public void display(Display display)  {

        try {
            display.print(readTemperature(), 1);
            display.writeDisplay();
            delay(3000);
            display.clear();
            display.print((int)readHumidity(),EBase.DEC);
        } catch (IOException e) {
            logger.error("display:",e);
        }
    }

    public synchronized double readHumidity() throws IOException {

        device.write((byte)HTU21DF_READHUM_HOLD);
        delay(50);

        byte[] buf = new byte[3];
        int r =  device.read(buf, 0, buf.length);
        if (r != buf.length) {
            logger.error("readHumidity: 3 bytes required, got {} bytes",r);
            throw new IOException("Cannot read humidity; r = " + r);
        }
        int msb = buf[0] & 0xFF;
        int lsb = buf[1] & 0xFF;
        int crc = buf[2] & 0xFF;

        int rawHumidity = ((msb << 8) + lsb) & 0xFFFC;


        double humidity = -6 + (125 * rawHumidity) / 65536.0;
        logger.debug("readHumidity: humidity = >{}<",humidity);
        return humidity;
    }

    public synchronized double readTemperature() throws IOException {
        device.write((byte)HTU21DF_READTEMP_HOLD);
        delay(50);

        byte[] buf = new byte[3];
        int r =  device.read(buf, 0, buf.length);
        if (r != buf.length) {
            logger.error("readTemperature: 3 bytes required, got {} bytes",r);
            throw new IOException("Cannot read temperature; r = " + r);
        }

        int msb = buf[0] & 0xFF;
        int lsb = buf[1] & 0xFF;
        int crc = buf[2] & 0xFF;
        int rawTemperature = ((msb << 8) + lsb) & 0xFFFC;

        double temp = -46.85F + (175.72 * rawTemperature) / 65536.0;

        return temp;
    }

    private class ReadDataTask implements Runnable {

        @Override
        public void run() {
            try {
                double temperature = readTemperature();
                if (lastTemperature != temperature) {
                    lastTemperature = temperature;
                    temperatureEvent.fire(new TemperatureEvent(ESensorPlace.INDOOR,getName(), temperature,TEMPERATURE_ACCURACY));
                }
                double humidity = readHumidity();
                if (lastHumidity != humidity) {
                    lastHumidity = humidity;
                    humidityEvent.fire(new HumidityEvent(ESensorPlace.INDOOR,getName(), humidity));
                }
            } catch (IOException e) {
                logger.error("run:",e);
            }
        }
    }

}
