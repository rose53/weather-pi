package de.rose53.pi.weatherpi.componets;

import static java.lang.Math.log;
import static java.lang.Math.log10;
import static java.lang.Math.pow;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;

import com.pi4j.gpio.extension.mcp.MCP3008Pin;
import com.pi4j.io.gpio.GpioPinAnalogInput;

import de.rose53.pi.weatherpi.Display;
import de.rose53.pi.weatherpi.ESensorPlace;
import de.rose53.pi.weatherpi.events.IlluminanceEvent;
import de.rose53.pi.weatherpi.utils.IntegerConfiguration;

@ApplicationScoped
public class LDRVT43N2 extends MCP3008Sensor implements Displayable, Sensor {

    private static final double FC2LUX = 10.764; //  one foot-candle is equal to one lumen per square foot or approximately 10.764 lux
    private static final double regressionData[][] = {{log10(1),log10(32)}, {log10(2),log10(19)}, {log10(4),log10(10)},
                                                      {log10(10),log10(5)}, {log10(100),log10(0.65)}};
    private static final double VREF                 = 3.3;
    private static final int RES_PULL_UP             = 2200; // 2,2kOhm


    @Inject
    Logger logger;

    @Inject
    @IntegerConfiguration(key = "ldrvt43n2.mcp3008pin", defaultValue = 0)
    int mcp3008pin;


    @Inject
    Event<IlluminanceEvent> illuminanceEvent;

    private GpioPinAnalogInput ldrPin;

    private double m;
    private double b;

    private double illuminance= 0;

    final ScheduledExecutorService clientProcessingPool = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init()  {
        // building a linear equation from the information in the data sheed of the VT43N2
        SimpleRegression regression = new SimpleRegression();

        regression.addData(regressionData);

        m = regression.getSlope();
        logger.debug("init: m = >{}<",m);

        b = regression.getIntercept();
        logger.debug("init: b = >{}<",b);

        ldrPin = gpio.provisionAnalogInputPin(gpioProvider, MCP3008Pin.ALL[mcp3008pin],"LDRSensor-A0");

        clientProcessingPool.scheduleAtFixedRate(new ReadDataTask(), 0, 30, TimeUnit.SECONDS);
    }

    @Override
    protected double getVRef() {
        return VREF;
    }

    @Override
    protected double getValue() {
        return ldrPin.getValue();
    }

    private double getResLDR() {
        return RES_PULL_UP / ((getVRef() / getVout())-1);
    }

    public double getLux() {
        double x = FC2LUX * pow(10,(log(getResLDR() / 1000) - b) / m);
        logger.debug("getLux: lux = >{}<",x);
        return x;
    }

    @Override
    public String getName() {
        return "LDRVT43N2";
    }

    @Override
    public void display(Display display) {
        try {
            display.print(getLux(), 1);
            display.writeDisplay();
        } catch (IOException e) {
            logger.error("display:",e);
        }
    }

    private class ReadDataTask implements Runnable {

        @Override
        public void run() {
            double value = getLux();

            if (illuminance != value) {
                illuminance = value;
                illuminanceEvent.fire(new IlluminanceEvent(ESensorPlace.INDOOR,getName(), illuminance));
            }
        }
    }
}
