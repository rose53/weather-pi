package de.rose53.pi.weatherpi.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface StringListConfiguration {

    @Nonbinding
    String key() default "";

    /**
     * Is it a mandatory property
     * @return true if mandatory
     */
    @Nonbinding
    boolean mandatory() default false;

}