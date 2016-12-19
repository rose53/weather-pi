package de.rose53.weatherpi.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegerConfiguration {

    @Nonbinding
    String key() default "";

    @Nonbinding
    int defaultValue() default 0;
}