package de.rose53.pi.weatherpi.componets;

import static de.rose53.pi.weatherpi.utils.Utils.delay;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.Display;
import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.configuration.IntegerConfiguration;
import de.rose53.pi.weatherpi.common.configuration.StringConfiguration;
import de.rose53.pi.weatherpi.display.EBase;
import de.rose53.pi.weatherpi.events.HumidityEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;

@ApplicationScoped
public class DHT22 /*implements Displayable, Sensor*/ {

    public final static double TEMPERATURE_ACCURACY = 0.5;


    @Inject
    Logger logger;

    @Inject
    @StringConfiguration(key = "dht22.gpioPin", defaultValue = "6")
    private String gpioPin;

    @Inject
    @IntegerConfiguration(key = "dht22.timeBetweenReads", defaultValue = 3000)
    private int timeBetweenReads;

    private double temperature = 0.0;
    private double humidity    = 0.0;

    final ScheduledExecutorService clientProcessingPool = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread retVal = new Thread(r,"DHT22 ReaderThread");
        retVal.setPriority(Thread.MIN_PRIORITY);
        return retVal;
    });

    @Inject
    Event<TemperatureEvent> temperatureEvent;

    @Inject
    Event<HumidityEvent> humidityEvent;

    @PostConstruct
    public void init()  {
        clientProcessingPool.scheduleAtFixedRate(new ReadDataTask(), 0, 30, TimeUnit.SECONDS);
    }


    private synchronized double[] readValues() throws IOException {
        logger.debug("readValues: called with options ...");
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        List<String> command = new ArrayList<>();
        command.add("./dht");
        command.add("22");
        command.add(gpioPin);

        ProcessBuilder pb = new ProcessBuilder(command);
        logger.debug("readValues: starting process ...");
        pb.redirectErrorStream(false);
        Process process = pb.start();

        copy(new BufferedInputStream(process.getInputStream()), o);
        double[] retVal = new double[0];
        try {
            if (process.waitFor() == 0) {
                logger.debug("readValues: process terminated, getting temperature and humidity data.");
                String result  = new String(o.toByteArray());
                logger.debug("readValues: data = >{}<",result);
                if (result.length() > 0) {
                    String[] parts= result.split(",");
                    retVal = new double[2];
                    retVal[0] = Double.valueOf(parts[0]);
                    retVal[1] = Double.valueOf(parts[1]);
                }
            } else {
                logger.error("auqireImage: raspistill terminated with an error");
                traceProcessOutput(process);
            }
        } catch (InterruptedException e) {
        }
        logger.debug("auqireImage: done.");
        return retVal;
    }

    public double readTemperature() throws IOException {
        return temperature;
    }

    public double readHumidity() throws IOException {
        return humidity;
    }

    //@Override
    public void display(Display display) {
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

    private void traceProcessOutput(Process process) throws IOException {
        InputStreamReader tempReader = new InputStreamReader(new BufferedInputStream(process.getErrorStream()));
        BufferedReader reader = new BufferedReader(tempReader);

        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            logger.trace(line);
        }
    }


    private long copy(InputStream input, OutputStream output) throws IOException {

        logger.debug("copy: start reading data from the input stream ...");
        long count = 0;
        int n = 0;
        byte[] buffer = new byte[4096];
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        logger.debug("copy: done, got bytes = {}",count);
        return count;
    }

    private class ReadDataTask implements Runnable {

        @Override
        public void run() {
            try {
                double[] values;

                int count = 0;
                while ((values = readValues()).length == 0 || (count < 5)) {
                    delay(timeBetweenReads);
                    count++;
                }

                if (values != null && values.length == 2) {
                    if (temperature != values[0]) {
                        temperature = values[0];
                        temperatureEvent.fire(new TemperatureEvent(ESensorPlace.INDOOR,"DHT22", temperature, TEMPERATURE_ACCURACY));
                    }
                    if (humidity != values[1]) {
                        humidity = values[1];
                        humidityEvent.fire(new HumidityEvent(ESensorPlace.INDOOR,"DHT22", humidity));
                    }
                }
            } catch (IOException e) {
                logger.error("run:",e);
            }
        }
    }
}
