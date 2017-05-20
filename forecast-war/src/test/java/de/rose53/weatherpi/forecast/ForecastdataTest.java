package de.rose53.weatherpi.forecast;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Test;

public class ForecastdataTest {



    @Test
    public void test() throws IOException, URISyntaxException {


        JsonObject forcastObject = Json.createReader(ForecastdataTest.class.getResource("/forecast.json").openStream()).readObject();
        assertNotNull(forcastObject);

        Forecastdata forecastData = new Forecastdata(forcastObject);
        assertNotNull(forecastData);

        assertEquals(forecastData.getLatitude(),48.5204,0.0);
        assertEquals(forecastData.getLongitude(),9.0491,0.0);
        assertEquals(forecastData.getTimezone(),"Europe/Berlin");
        assertEquals(forecastData.getOffset(),1);

        Currently currently = forecastData.getCurrently();
        assertNotNull(currently);
        assertEquals(currently.getTime(),1446406533);
        assertNull(currently.getSunriseTime());
        assertNull(currently.getSunsetTime());
        assertNull(currently.getMoonPhase());
    }

}
