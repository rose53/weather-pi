package de.rose53.pi.weatherpi.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

public class ConfigurationInjectionManager {

	static final String INVALID_KEY="Invalid key '{0}'";
    static final String MANDATORY_PARAM_MISSING = "No definition found for a mandatory configuration parameter : '{0}'";
    private final Properties properties = new Properties();

    @PostConstruct
    public void init() {

    	try (FileInputStream file = new FileInputStream("./configuration.properties");) {
            //load all the properties from this file
            properties.load(file);
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
                if (param.mandatory())
                    throw new IllegalStateException(MessageFormat.format(MANDATORY_PARAM_MISSING, new Object[]{param.key()}));
                else
                    return param.defaultValue();
            }
            return value;
        } catch (MissingResourceException e) {
            if (param.mandatory()) throw new IllegalStateException(MessageFormat.format(MANDATORY_PARAM_MISSING, new Object[]{param.key()}));
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
                if (param.mandatory())
                    throw new IllegalStateException(MessageFormat.format(MANDATORY_PARAM_MISSING, new Object[]{param.key()}));
                else
                    return param.defaultValue();
            }
            return Integer.valueOf(value);
        } catch (MissingResourceException e) {
            if (param.mandatory()) throw new IllegalStateException(MessageFormat.format(MANDATORY_PARAM_MISSING, new Object[]{param.key()}));
            return Integer.MIN_VALUE;
        }
    }
}