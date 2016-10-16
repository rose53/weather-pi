package de.rose53.weatherpi.configuration;

import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;

public class ConfigurationExposer {

    @Inject
    Logger logger;

    @Produces
    @StringConfiguration
    public String stringConfiguration(InjectionPoint ip) {
        StringConfiguration param = ip.getAnnotated().getAnnotation(StringConfiguration.class);
        if (param.key() == null || param.key().length() == 0) {
            return param.defaultValue();
        }

        Client clientBuilder = ClientBuilder.newClient();

        UriBuilder uriBuilder = UriBuilder.fromUri("http://localhost:8080/configuration/resources/configurations/string");

        uriBuilder.segment(param.key());

        Response response = clientBuilder.target(uriBuilder)
                                         .request(MediaType.APPLICATION_JSON_TYPE)
                                         .get();

        String retVal = param.defaultValue();


        if (Response.Status.OK == response.getStatusInfo()) {
            retVal = response.readEntity(String.class);
        }
        response.close();
        return retVal;
    }

    @Produces
    @StringListConfiguration
    public List<String> stringListConfiguration(InjectionPoint ip) {
        StringListConfiguration param = ip.getAnnotated().getAnnotation(StringListConfiguration.class);
        if (param.key() == null || param.key().length() == 0) {
            return Collections.emptyList();
        }
        Client clientBuilder = ClientBuilder.newClient();

        UriBuilder uriBuilder = UriBuilder.fromUri("http://localhost:8080/configuration/resources/configurations/stringlist");

        uriBuilder.segment(param.key());

        Response response = clientBuilder.target(uriBuilder)
                                         .request(MediaType.APPLICATION_JSON_TYPE)
                                         .get();

        List<String> retVal = Collections.emptyList();
        if (Response.Status.OK == response.getStatusInfo()) {
            retVal = response.readEntity(new GenericType<List<String>>() {});
        }
        response.close();
        return retVal;
    }
}