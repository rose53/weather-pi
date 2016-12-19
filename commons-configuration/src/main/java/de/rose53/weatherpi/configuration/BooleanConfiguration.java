package de.rose53.weatherpi.configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface BooleanConfiguration {

    @Nonbinding
    String key() default "";

    /**
     * Default value if not provided
     * @return default value or false
     */
    @Nonbinding
    boolean defaultValue() default false;
}