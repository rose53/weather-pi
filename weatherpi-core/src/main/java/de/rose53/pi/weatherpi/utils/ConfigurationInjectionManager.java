package de.rose53.pi.weatherpi.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.slf4j.Logger;

public class ConfigurationInjectionManager {

    static final String INVALID_KEY = "Invalid key '{0}'";
    static final String MANDATORY_PARAM_MISSING = "No definition found for a mandatory configuration parameter : '{0}'";

    private final Properties properties = new Properties();

    @Inject
    Logger logger;

    @PostConstruct
    public void init() {

        try (FileInputStream file = new FileInputStream("./configuration.properties")) {
            //load all the properties from this file
            properties.load(file);
        } catch (IOException e) {
            logger.error("init:",e);
        }
    }

    @Produces
    @StringConfiguration
    public String stringConfiguration(InjectionPoint ip) throws IllegalStateException {
        StringConfiguration param = ip.getAnnotated().getAnnotation(StringConfiguration.class);
        if (param.key() == null || param.key().length() == 0) {
            return param.defaultValue();
        }
        String value;
        try {
            value = properties.getProperty(param.key());
            if (value == null || value.trim().length() == 0) {
                if (param.mandatory()) {
                    throw new IllegalStateException(MessageFormat.format(MANDATORY_PARAM_MISSING, new Object[]{param.key()}));
                }
                return param.defaultValue();
            }
            return value;
        } catch (MissingResourceException e) {
            if (param.mandatory()) {
                throw new IllegalStateException(MessageFormat.format(MANDATORY_PARAM_MISSING, new Object[]{param.key()}));
            }
            return MessageFormat.format(INVALID_KEY, new Object[]{param.key()});
        }
    }

    @Produces
    @IntegerConfiguration
    public int intConfiguration(InjectionPoint ip) throws IllegalStateException {
        IntegerConfiguration param = ip.getAnnotated().getAnnotation(IntegerConfiguration.class);
        if (param.key() == null || param.key().length() == 0) {
            return param.defaultValue();
        }
        String value;
        try {
            value = properties.getProperty(param.key());
            if (value == null || value.trim().length() == 0) {
                if (param.mandatory()) {
                    throw new IllegalStateException(MessageFormat.format(MANDATORY_PARAM_MISSING, new Object[]{param.key()}));
                }
                return param.defaultValue();
            }
            return Integer.valueOf(value);
        } catch (MissingResourceException e) {
            if (param.mandatory()) {
                throw new IllegalStateException(MessageFormat.format(MANDATORY_PARAM_MISSING, new Object[]{param.key()}));
            }
            return Integer.MIN_VALUE;
        }
    }

    @Produces
    @BooleanConfiguration
    public boolean booleanConfiguration(InjectionPoint ip) throws IllegalStateException {
        BooleanConfiguration param = ip.getAnnotated().getAnnotation(BooleanConfiguration.class);
        if (param.key() == null || param.key().length() == 0) {
            return param.defaultValue();
        }
        String value;
        try {
            value = properties.getProperty(param.key());
            if (value == null || value.trim().length() == 0) {
                if (param.mandatory()) {
                    throw new IllegalStateException(MessageFormat.format(MANDATORY_PARAM_MISSING, new Object[]{param.key()}));
                }
                return param.defaultValue();
            }
            return Boolean.valueOf(value);
        } catch (MissingResourceException e) {
            if (param.mandatory()) {
                throw new IllegalStateException(MessageFormat.format(MANDATORY_PARAM_MISSING, new Object[]{param.key()}));
            }
            return param.defaultValue();
        }
    }

    @Produces
    @StringListConfiguration
    public List<String> stringListConfiguration(InjectionPoint ip) throws IllegalStateException {
        StringListConfiguration param = ip.getAnnotated().getAnnotation(StringListConfiguration.class);
        if (param.key() == null || param.key().length() == 0) {
            return Collections.emptyList();
        }
        return properties.entrySet().stream().filter(e -> e.getKey().toString().startsWith(param.key()))
                                             .map(e -> e.getValue().toString())
                                             .collect(Collectors.toList());
    }
}