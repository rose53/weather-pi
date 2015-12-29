package de.rose53.pi.weatherpi.componets;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.pi4j.gpio.extension.mcp.MCP3008Pin;
import com.pi4j.io.gpio.GpioPinAnalogInput;

import de.rose53.pi.weatherpi.Display;
import de.rose53.pi.weatherpi.common.configuration.IntegerConfiguration;

@ApplicationScoped
public class TGS2600 extends MCP3008Sensor implements Displayable, Sensor {

    private static final double VREF                 = 5.0;
    private static final int RES_PULL_UP             = 22000; // 22kOhm

    @Inject
    Logger logger;

    @Inject
    @IntegerConfiguration(key = "tgs2600.mcp3008pin", defaultValue = 0)
    int mcp3008pin;

    private GpioPinAnalogInput tgsPin;

    @PostConstruct
    public void init()  {
        tgsPin = gpio.provisionAnalogInputPin(gpioProvider, MCP3008Pin.ALL[mcp3008pin],"TGS2600Sensor-A1");
    }

    @Override
    protected double getVRef() {
        return VREF;
    }

    @Override
    protected double getValue() {
        return tgsPin.getValue();
    }

    private double getRes() {
        return RES_PULL_UP * ((getVRef() / getVout())-1);
    }

    @Override
    public String getName() {
        return "TGS2600";
    }

    @Override
    public void display(Display display) {
        double rs = getRes();
        logger.debug("display: rs = >{}<",rs);
        try {
             display.print(rs/1000,1);
            display.writeDisplay();
        } catch (IOException e) {
            logger.error("display:",e);
        }
    }

}
