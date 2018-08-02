package de.rose53.pi.weatherpi.common;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.rose53.pi.weatherpi.common.LoggerExposer;

@RunWith(CdiRunner.class)
@AdditionalClasses(LoggerExposer.class)
public class HeatindexCalculatorTest {

    @Inject
    HeatindexCalculator calculator;

    @Test
    public void testCalculateLowTemp() {
        assertNull(calculator.calculate(26.9, 50.0));
    }

    @Test
    public void testCalculateLowHumidity() {
        assertNull(calculator.calculate(27.0, 39.9));
    }

    @Test
    public void testCalculate() {
        assertEquals(27,calculator.calculate(27.0, 40),0.5);
        assertEquals(58,calculator.calculate(36.0, 75),0.5);
    }
}
