package de.rose53.pi.weatherpi.componets;

import static java.lang.Math.*;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.slf4j.Logger;

import com.pi4j.gpio.extension.mcp.MCP3008GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP3008Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinAnalogInput;

import de.rose53.pi.weatherpi.Display;

@ApplicationScoped
public class LDRVT43N2 implements Displayable {

	private static final double FC2LUX = 10.764; //  one foot-candle is equal to one lumen per square foot or approximately 10.764 lux
	private static final double regressionData[][] = {{log10(1),log10(32)}, {log10(2),log10(19)}, {log10(4),log10(10)},
			                                          {log10(10),log10(5)}, {log10(100),log10(0.65)}};
	private static final double VREF                 = 3.3;
	private static final int RES_PULL_UP             = 2200; // 2,2kOhm
	private static final int MCP3008_RANGE_MAX_VALUE = 1024;

	@Inject
    Logger logger;


	@Inject
	GpioController gpio;

	@Inject
	MCP3008GpioProvider gpioProvider;

	private GpioPinAnalogInput ldrPin;

	private double m;
	private double b;

	@PostConstruct
    public void init()  {

		// building a linear equation from the information in the datasheed of the VT43N2
		SimpleRegression regression = new SimpleRegression();

		regression.addData(regressionData);

		m = regression.getSlope();
		logger.debug("init: m = >{}<",m);

		b = regression.getIntercept();
		logger.debug("init: b = >{}<",b);

        ldrPin = gpio.provisionAnalogInputPin(gpioProvider, MCP3008Pin.CH0,"LDRSensor-A0");

	}

	private double getVout() {
		return VREF * (ldrPin.getValue() /  MCP3008_RANGE_MAX_VALUE);
	}

	private double getResLDR() {
		return RES_PULL_UP / ((VREF / getVout())-1);
	}

	public double getLux() {
		double x = FC2LUX * pow(10,(log(getResLDR() / 1000) - b) / m);
		logger.debug("getLux: lux = >{}<",x);
		return x;
	}

	@Override
	public void display(Display display) {

		try {
	        display.print(getLux(), 1);
			display.writeDisplay();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
