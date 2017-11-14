//////////////////////////////////////////////////////////////////
//                  C O P Y R I G H T (c) 2016                  //
//               A G F A - G E V A E R T G R O U P              //
//                     All Rights Reserved                      //
//////////////////////////////////////////////////////////////////
//                                                              //
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF               //
//               AGFA Healthcare                                //
// The copyright notice above does not evidence any             //
// actual or intended publication of such source code.          //
//                                                              //
//////////////////////////////////////////////////////////////////
package de.rose53.pi.weatherpi.common;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;


@ApplicationScoped
public class RestResourceProducer {

    @Inject
    Logger logger;

    private String hostPort;


    @PostConstruct
    void init() {
        ServerAddressDetector serverAddressDetector = new ServerAddressDetector();
        hostPort = serverAddressDetector.getServerAddress();
        logger.debug("init: using rest hostport={}", hostPort);
    }


    @Produces
    @RestResource
    public WebTarget g5RestResource(InjectionPoint ip) {
       logger.debug("g5RestResource: id={}",ip);
       RestResource param = ip.getAnnotated().getAnnotation(RestResource.class);

       StringBuilder builder = new StringBuilder("http://");
       builder.append(hostPort)
              .append(param.path());

       logger.debug("g5RestResource: uri={}", builder.toString());
       return ClientBuilder.newBuilder()
                           .build()
                           .target(builder.toString());
    }

}
