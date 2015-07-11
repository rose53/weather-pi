package de.rose53.pi.weatherpi.pi4j.gpio.extension.ads;

import java.io.IOException;

import javax.enterprise.inject.Produces;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

public class MCP3008GPIOExposer {

	  @Produces
	    public MCP3008GpioProvider exposeMCP3008GpioProvider() {
	        try {
				return new MCP3008GpioProvider(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return null;
	    };
}
