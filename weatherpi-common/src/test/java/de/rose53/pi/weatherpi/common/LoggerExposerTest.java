package de.rose53.pi.weatherpi.common;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.junit.WeldJUnit4Runner;

@RunWith(WeldJUnit4Runner.class)
public class LoggerExposerTest {

    @Inject
    Logger logger;

    @Test
    public void testNotNull() {
        assertNotNull(logger);
    }

}
