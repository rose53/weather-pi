package de.rose53.weatherpi.statistics.control;

import static org.junit.Assert.*;

import static java.util.Collections.*;
import static java.util.Arrays.*;
import static org.hamcrest.CoreMatchers.*;
import static de.rose53.weatherpi.statistics.control.EClimatologicClassificationDay.*;

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
