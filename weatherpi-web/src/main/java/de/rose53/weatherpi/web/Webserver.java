package de.rose53.weatherpi.web;

import java.util.EnumSet;

import javax.inject.Inject;
import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import de.rose53.pi.weatherpi.common.configuration.IntegerConfiguration;

public class Webserver {

    @Inject
    @IntegerConfiguration(key = "web.port", defaultValue = 8080)
    private int port;

    @SuppressWarnings("unused")
    @Inject
    private SensorDataCdiHelper sensorDataCdiHelper; // needed for missing JaxRs CDI integration

    @SuppressWarnings("unused")
    @Inject
    private ForecastCdiHelper forecastCdiHelper; // needed for missing JaxRs CDI integration

    private Server server;

    public void start() throws Exception  {

        server = new Server(port);

        ServletContextHandler ctx = new ServletContextHandler();
        ctx.setContextPath("/websocket/");
        ctx.addServlet(SensorEventSocketServlet.class, "/sensorevents");

        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/rest/");

        contextHandler.setInitParameter("resteasy.servlet.mapping.prefix","/resources");
        contextHandler.addFilter(CrossOriginFilter.class, "/resources/*",EnumSet.of(DispatcherType.REQUEST));

        ServletHolder restEasyServletHolder = new ServletHolder(new HttpServletDispatcher());

        restEasyServletHolder.setInitOrder(1);
        restEasyServletHolder.setInitParameter("javax.ws.rs.Application","de.rose53.weatherpi.web.JaxRsApplication");

        contextHandler.addServlet(restEasyServletHolder, "/resources/*");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
        resourceHandler.setResourceBase(Webserver.class.getResource("/webapp").toExternalForm());

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, ctx, contextHandler});
        server.setHandler(handlers);

        server.start();
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