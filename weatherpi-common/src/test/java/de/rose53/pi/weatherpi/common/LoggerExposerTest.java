package de.rose53.pi.weatherpi.common;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

@RunWith(CdiRunner.class)
@AdditionalClasses(LoggerExposer.class)
public class LoggerExposerTest {

    @Inject
    Logger logger;

    @Test
    public void testNotNull() {
        assertNotNull(logger);
    }

}
