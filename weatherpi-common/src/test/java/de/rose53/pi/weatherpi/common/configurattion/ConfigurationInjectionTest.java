package de.rose53.pi.weatherpi.common.configurattion;

import static org.junit.Assert.*;

import java.util.List;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.rose53.pi.weatherpi.common.configuration.StringListConfiguration;
import de.rose53.pi.weatherpi.common.junit.WeldJUnit4Runner;

@RunWith(WeldJUnit4Runner.class)
public class ConfigurationInjectionTest {

    @Inject
    @StringListConfiguration
    List<String> stringListConfigurationEmptyKey;

    @Inject
    @StringListConfiguration(key="stringlist")
    List<String> stringListConfiguration;

    @Test
    public void testStringListConfigurationEmptyKey() {
        assertNotNull(stringListConfigurationEmptyKey);
        assertTrue(stringListConfigurationEmptyKey.isEmpty());
    }

    @Test
    public void testStringListConfiguration() {
        assertNotNull(stringListConfiguration);
        assertTrue(!stringListConfiguration.isEmpty());
    }

}
