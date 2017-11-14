package de.rose53.pi.weatherpi.common;

import java.lang.management.ManagementFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerAddressDetector {

    static Logger logger = LoggerFactory.getLogger(ServerAddressDetector.class);

    /**
     * Returns the host and port for the actual server.
     *
     * @return returns {@code <host>:<port>}, defaults to localhost:6444
     */
    public String getServerAddress() {
        // get the host and port
        Object tmpHost = "127.0.0.1";
        Object tmpPort = "8080";

        // host name does not work if bind adress specified and <> 0.0.0.0
        String bindadr = System.getProperty("jboss.bind.address");

        tmpHost = System.getProperty("jboss.qualified.host.name", System.getProperty("jboss.host.name", null));

        if (bindadr != null && !bindadr.equals("0.0.0.0")) {
            tmpHost = bindadr;
        }

        final String portOffsetProperty = System.getProperty("jboss.socket.binding.port-offset", "0");
        final String portProperty = System.getProperty("jboss.http.port", "8080");
        long portOffset = 0;
        try {
            if (portOffsetProperty != null) { // may be sombody omitted this param
                portOffset = Long.parseLong(portOffsetProperty);
            }
            tmpPort = Long.parseLong(portProperty) + portOffset;
        } catch (final NumberFormatException e) {
            logger.warn("Cannot parse server port of portoffset via properties: {} - will try JMX",e.getLocalizedMessage(), e);
        }

        /*
         * Getting host and port via properties seems to have failed - now try via JMX.
         */
        if (tmpHost == null || tmpPort == null) {
            tmpHost = "127.0.0.1"; // localhost can be redefinedy by entry in file hosts, works on server
            try {
                tmpHost = ManagementFactory.getPlatformMBeanServer()
                        .getAttribute(new ObjectName("jboss.as:interface=public"), "inet-address");
                tmpPort = ManagementFactory.getPlatformMBeanServer().getAttribute(
                        new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http"), "port");
            } catch (AttributeNotFoundException | InstanceNotFoundException | MalformedObjectNameException
                    | MBeanException | ReflectionException e) {
                logger.error("Failed to get host and port via JMX - will use defaults", e);
            }
        }
        // tmphost is bindadr or hostname or 127.0.0.1

        if (tmpPort == null) {
            tmpPort = "8080";
        }

        String retVal = String.format("%s:%s", tmpHost, tmpPort);
        logger.debug("getServerAddress: returning >{}<", retVal);
        return retVal;
    }

}
