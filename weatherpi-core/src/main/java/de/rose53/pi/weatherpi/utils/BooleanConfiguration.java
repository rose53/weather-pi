package de.rose53.pi.weatherpi.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface BooleanConfiguration {

    /**
     * Bundle key
     * @return a valid bundle key or ""
     */
    @Nonbinding
    String key() default "";

    /**
     * Is it a mandatory property
     * @return true if mandatory
     */
    @Nonbinding
    boolean mandatory() default false;

    /**
     * Default value if not provided
     * @return default value or false
     */
    @Nonbinding
    boolean defaultValue() default false;
}