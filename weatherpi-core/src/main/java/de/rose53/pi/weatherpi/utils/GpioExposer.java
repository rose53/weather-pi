package de.rose53.pi.weatherpi.utils;

import javax.enterprise.inject.Produces;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

public class GpioExposer {


    @Produces
    public GpioController exposeGpioController() {
        return GpioFactory.getInstance();
    };


}
