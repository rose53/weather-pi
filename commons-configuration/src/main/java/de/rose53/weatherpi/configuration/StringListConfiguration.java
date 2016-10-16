package de.rose53.weatherpi.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface StringListConfiguration {

    @Nonbinding
    String key() default "";
}