package de.rose53.pi.weatherpi.common;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.rose53.pi.weatherpi.common.LoggerExposer;
import de.rose53.pi.weatherpi.common.WindchillCalculator;

@RunWith(CdiRunner.class)
@AdditionalClasses(LoggerExposer.class)
public class WindchillCalculatorTest {

    @Inject
    WindchillCalculator calculator;

    @Test
    public void testCalculateLowWind() {
        assertNull(calculator.calculate(0.0, 2.0));
    }

    @Test
    public void testCalculateHightTemp() {
        assertNull(calculator.calculate(11.0, 5.0));
    }

    @Test
    public void testCalculate() {
        assertEquals(9.8,calculator.calculate(10.0, 5.0),0.05);
        assertEquals(-27.4,calculator.calculate(-15.0, 40.0),0.05);
    }
}
