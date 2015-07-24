package de.rose53.pi.weatherpi.componets;

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
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;


import de.rose53.pi.weatherpi.Display;
import de.rose53.pi.weatherpi.display.EBase;
import de.rose53.pi.weatherpi.events.PressureEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;

@Singleton
public class DHT22 implements Displayable {

    @Inject
    Logger logger;

    private double temperature = 0.0;
    private double humidity    = 0.0;

    final ScheduledExecutorService clientProcessingPool = Executors.newScheduledThreadPool(1);

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
        command.add("6");

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
                    retVal[0] = Double.valueOf(result.split(",")[0]);
                    retVal[1] = Double.valueOf(result.split(",")[1]);
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

    @Override
    public void display(Display display) {
        try {
            display.print(readTemperature(), 1);
            display.writeDisplay();
            delay(3000);
            display.clear();
            display.print((int)readHumidity(),EBase.DEC);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
                    double[] values;

                    int count = 0;
                    while ((values = readValues()).length == 0 || (count < 5)) {
                        delay(3000);
                        count++;
                    }

                    if (values != null && values.length == 2) {
                        temperature = values[0];
                        humidity = values[1];
                    }
                    //temperatureEvent.fire(new TemperatureEvent("BMP085", readTemperature()));
                    //pressureEvent.fire(new PressureEvent("BMP085", readNormalizedPressure()));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


        }
    }
}
