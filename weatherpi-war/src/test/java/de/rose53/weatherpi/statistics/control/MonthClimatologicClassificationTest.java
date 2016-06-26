package de.rose53.weatherpi.statistics.control;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static java.util.Arrays.*;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Month;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rose53.weatherpi.statistics.entity.DayStatisticBean;

public class MonthClimatologicClassificationTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBuild() {

        DayStatisticBean s1 = new DayStatisticBean();
        s1.setDay(Date.valueOf(LocalDate.of(2016, 3, 20)));
        s1.settMax(20.0);
        s1.settMin(8.0);
        s1.settMed(10.0);

        DayStatisticBean s2 = new DayStatisticBean();
        s2.setDay(Date.valueOf(LocalDate.of(2016, 3, 21)));
        s2.settMax(26.0);
        s2.settMin(8.0);
        s2.settMed(10.0);

        DayStatisticBean s3 = new DayStatisticBean();
        s3.setDay(Date.valueOf(LocalDate.of(2016, 2, 21)));
        s3.settMax(26.0);
        s3.settMin(8.0);
        s3.settMed(10.0);


        assertThat(MonthClimatologicClassification.build(Month.MARCH, asList(s1,s2,s3)),is(notNullValue()));

    }

}
