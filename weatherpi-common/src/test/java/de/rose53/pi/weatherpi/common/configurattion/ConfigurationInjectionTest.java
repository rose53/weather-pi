package de.rose53.pi.weatherpi.common.configurattion;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.rose53.pi.weatherpi.common.LoggerExposer;
import de.rose53.pi.weatherpi.common.configuration.ConfigurationInjectionManager;
import de.rose53.pi.weatherpi.common.configuration.StringListConfiguration;

@RunWith(CdiRunner.class)
@AdditionalClasses({LoggerExposer.class,ConfigurationInjectionManager.class})
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
