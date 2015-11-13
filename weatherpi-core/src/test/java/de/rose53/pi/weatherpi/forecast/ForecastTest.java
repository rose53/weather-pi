package de.rose53.pi.weatherpi.forecast;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ForecastTest {

    @Test
    @Ignore
    public void testGetForecastdata() throws JsonParseException, JsonMappingException, IOException {

        ForecastIO.Builder builder = new ForecastIO.Builder("apikey","lat","long");

        ForecastIO forecast = builder.units("si")
                                     .lang("de")
                                     .build();

        Forecastdata data = forecast.getForecastdata();
        assertNotNull(data);
    }

}

