/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.rose53.pi.weatherpi;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.slf4j.Logger;



/**
 *
 * @author rose
 */
@Singleton
public class WeatherPi implements Runnable {

    @Inject
    Logger logger;

    private boolean running;




//    @Inject
//    Webserver webServer;


    public WeatherPi() {

    }

    public void start() {

        try {
            System.out.print("Starting WebServer ...");
 //           webServer.start();
            System.out.println("\b\b\bdone.");

            System.out.print("Starting JoystickServer ...");
 //           joystickServer.start();
            System.out.println("\b\b\bdone.");

        } catch (Exception e) {
            logger.error("start:",e);
        }
        show();

        System.out.println("\n\n ####################################################### ");
        System.out.println(" ####              WEATHER PI IS ALIVE !!!           ### ");
        System.out.println(" ####################################################### ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yy hh:mm:ss");
        System.out.println(" ### Date: " + dateFormat.format(new Date()));
        running = true;
    }

    public void stop() {
        if (running) {
            running = false;
        }
        try {
           // webServer.stop();
        } catch (Exception e) {
            logger.error("stop: ",e);
        }
    }

    @Override
    public void run() {
        start();

        while (running) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.warn("run:",e);
            }
        }
    }

    public void show() {
    }



    public static void main(String[] args) {
        System.out.println("Starting ...");

        System.out.print("Initializing CDI ...");
        final Weld weld = new Weld();
        WeldContainer container = weld.initialize();

        WeatherPi marvin = container.instance().select(WeatherPi.class).get();
        System.out.println("\b\b\bdone.");

        System.out.println("Starting Marvin ...");
        Thread thread = new Thread(marvin);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                marvin.logger.info("Shutdown Hook is running !");
                marvin.stop();
                weld.shutdown();
            }
        });

    }
}
