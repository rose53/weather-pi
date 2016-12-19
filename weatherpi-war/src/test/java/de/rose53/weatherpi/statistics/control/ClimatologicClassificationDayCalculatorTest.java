package de.rose53.weatherpi.statistics.control;

import static org.junit.Assert.*;

import java.util.List;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.rose53.pi.weatherpi.common.EClimatologicClassificationDay;
import de.rose53.pi.weatherpi.common.LoggerExposer;
import de.rose53.weatherpi.statistics.entity.DayStatisticBean;

@RunWith(CdiRunner.class)
@AdditionalClasses(LoggerExposer.class)
public class ClimatologicClassificationDayCalculatorTest {

    @Inject
    ClimatologicClassificationDayCalculator calculator;

    @Test
    public void testCalculateClimatologicClassificationDayBeanNull() {
        assertNotNull(calculator.calculateClimatologicClassificationDay(null));
        assertTrue(calculator.calculateClimatologicClassificationDay(null).isEmpty());
    }

    @Test
    public void testCalculateClimatologicClassificationDayBeanNullTemp() {
        DayStatisticBean bean = new DayStatisticBean();
        assertNotNull(calculator.calculateClimatologicClassificationDay(bean));
        assertTrue(calculator.calculateClimatologicClassificationDay(bean).isEmpty());
    }

    @Test
    public void testCalculateClimatologicClassificationDay() {
        List<EClimatologicClassificationDay> result = calculator.calculateClimatologicClassificationDay(-5.0,20.0,7.0);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

}
