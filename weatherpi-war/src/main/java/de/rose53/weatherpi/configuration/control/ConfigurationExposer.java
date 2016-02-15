package de.rose53.weatherpi.configuration.control;

import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.weatherpi.configuration.StringConfiguration;
import de.rose53.weatherpi.configuration.StringListConfiguration;
import de.rose53.weatherpi.configuration.boundary.ConfigurationService;

public class ConfigurationExposer {

    @Inject
    Logger logger;

    @Inject
    ConfigurationService configurationService;

    @Produces
    @StringConfiguration
    public String stringConfiguration(InjectionPoint ip) throws IllegalStateException {
        StringConfiguration param = ip.getAnnotated().getAnnotation(StringConfiguration.class);
        if (param.key() == null || param.key().length() == 0) {
            return param.defaultValue();
        }
        return configurationService.getValue(param.key());
    }

    @Produces
    @StringListConfiguration
    public List<String> stringListConfiguration(InjectionPoint ip) throws IllegalStateException {
        StringListConfiguration param = ip.getAnnotated().getAnnotation(StringListConfiguration.class);
        if (param.key() == null || param.key().length() == 0) {
            return Collections.emptyList();
        }
        return configurationService.getValues(param.key());
    }
}