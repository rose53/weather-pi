package de.rose53.pi.weatherpi.componets;

import javax.inject.Inject;

import com.pi4j.gpio.extension.mcp.MCP3008GpioProvider;
import com.pi4j.io.gpio.GpioController;

public abstract class MCP3008Sensor {

	protected static final int MCP3008_RANGE_MAX_VALUE = 1024;

	@Inject
	GpioController gpio;

	@Inject
	MCP3008GpioProvider gpioProvider;

	abstract protected double getVRef();
	abstract protected double getValue();

	protected double getVout() {
		return getVRef() * (getValue() /  MCP3008_RANGE_MAX_VALUE);
	}
}
