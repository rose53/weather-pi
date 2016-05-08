package de.rose53.weatherpi.statistics.control;

import static org.junit.Assert.*;

import javax.inject.Inject;

import static org.hamcrest.Matchers.*;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.rose53.pi.weatherpi.common.LoggerExposer;

@RunWith(CdiRunner.class)
@AdditionalClasses(LoggerExposer.class)
public class HeatIndexCalculatorTest {

    @Inject
    HeatIndexCalculator calculator;

    @Test
    public void calculateLowTemp() {
        assertThat(calculator.calculate(10.0, 50.0),nullValue());
    }

    @Test
    public void calculate() {
        assertThat(calculator.calculate(31.0, 45.0),equalTo(32L));
        assertThat(calculator.calculate(31.0, 45.0),equalTo(32L));
    }
}
