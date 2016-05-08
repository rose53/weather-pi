package de.rose53.weatherpi.statistics.control;

import static de.rose53.weatherpi.statistics.control.EClimatologicClassificationDay.DESERT_DAY;
import static de.rose53.weatherpi.statistics.control.EClimatologicClassificationDay.MAIN_VEGETATION_DAY;
import static de.rose53.weatherpi.statistics.control.EClimatologicClassificationDay.TROPICAL_NIGHT;
import static de.rose53.weatherpi.statistics.control.EClimatologicClassificationDay.getTwitterFeed;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EClassificationDayTest {

    @Test
    public void testGetTwitterFeedNull() {
        assertThat(getTwitterFeed(null),is(equalTo("")));
    }

    @Test
    public void testGetTwitterFeedEmpty() {
        assertThat(getTwitterFeed(emptyList()),is(equalTo("")));
    }

    @Test
    public void testGetTwitterFeed() {
        assertThat(getTwitterFeed(asList(DESERT_DAY,TROPICAL_NIGHT,MAIN_VEGETATION_DAY)),is(equalTo("desert,tropical night,main vegetation")));
    }
}
