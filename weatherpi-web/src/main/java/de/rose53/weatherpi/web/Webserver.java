package de.rose53.weatherpi.web;

import java.net.URL;

import javax.inject.Inject;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import de.rose53.pi.weatherpi.utils.IntegerConfiguration;

public class Webserver {

    @Inject
    @IntegerConfiguration(key = "web.port", defaultValue = 8080)
    private int port;

    private Server server;



    public void start() throws Exception {

//        System.setProperty("java.naming.factory.url","org.eclipse.jetty.jndi");
//        System.setProperty("java.naming.factory.initial","org.eclipse.jetty.jndi.InitialContextFactory");


        server = new Server(port);

        ServletContextHandler ctx = new ServletContextHandler();
        ctx.setContextPath("/");
        ctx.addServlet(SensorEventSocketServlet.class, "/sensorevents");

//        ServletContextHandler contexHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
//        contexHandler.setContextPath("/marvin/resources");
//        ServletHolder restEasyServletHolder = new ServletHolder(new HttpServletDispatcher());

//        restEasyServletHolder.setInitParameter("javax.ws.rs.Application","de.rose53.marvin.server.JaxRsApplication");

//        contexHandler.addServlet(restEasyServletHolder, "/*");
//        context.addEventListener(new Listener());
//        context.addEventListener(new BeanManagerResourceBindingListener());

 //       final HandlerList handlers = new HandlerList();
 //       handlers.setHandlers(new Handler[] { contextHandler });

        String  baseStr  = "/webapp";  //... contains: helloWorld.html, login.html, etc. and folder: other/xxx.html
        URL    baseUrl  = Webserver.class.getResource( baseStr );
        String  basePath = baseUrl.toExternalForm();

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "index.html" });

        resourceHandler.setResourceBase(basePath);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, ctx/*, contexHandler */});
        server.setHandler(handlers);

        server.start();



//        new Resource("BeanManager", new Reference("javax.enterprise.inject.spi.BeanMnanager",
//                "org.jboss.weld.resources.ManagerObjectFactory", null));
        //server.join();
    }

    public void stop() throws Exception {
        if (server != null) {
            server.stop();
        }
    }


    public static class SensorEventSocketServlet extends WebSocketServlet {

        private static final long serialVersionUID = 2805587332238800859L;

        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.register(SensorEvent.class);
        }
    }
}