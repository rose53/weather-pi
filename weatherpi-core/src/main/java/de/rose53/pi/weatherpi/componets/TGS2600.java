package de.rose53.pi.weatherpi.componets;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.pi4j.gpio.extension.mcp.MCP3008Pin;
import com.pi4j.io.gpio.GpioPinAnalogInput;

import de.rose53.pi.weatherpi.Display;

@ApplicationScoped
public class TGS2600 extends MCP3008Sensor implements Displayable {

	private static final double VREF                 = 5.0;
	private static final int RES_PULL_UP             = 22000; // 22kOhm

	@Inject
    Logger logger;

	private GpioPinAnalogInput tgsPin;

	@PostConstruct
    public void init()  {

		tgsPin = gpio.provisionAnalogInputPin(gpioProvider, MCP3008Pin.CH1,"TGS2600Sensor-A1");

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
	public void display(Display display) {
		double rs = getRes();
		logger.debug("display: rs = >{}<",rs);
	}

}
