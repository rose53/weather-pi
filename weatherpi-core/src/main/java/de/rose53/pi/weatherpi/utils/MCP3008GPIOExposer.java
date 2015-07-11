package de.rose53.pi.weatherpi.utils;

import java.io.IOException;

import javax.enterprise.inject.Produces;

import com.pi4j.gpio.extension.mcp.MCP3008GpioProvider;
import com.pi4j.io.spi.SpiChannel;

public class MCP3008GPIOExposer {

	  @Produces
	    public MCP3008GpioProvider exposeMCP3008GpioProvider() {
	        try {
				return new MCP3008GpioProvider(SpiChannel.CS0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return null;
	    };
}
