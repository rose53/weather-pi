package de.rose53.pi.weatherpi.forecast;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.rose53.weatherpi.forecast.Forecastdata;

public class ForecastdataTest {



    @Test
    public void test() throws IOException, URISyntaxException {

        String jsonString = new String(Files.readAllBytes(Paths.get(ForecastdataTest.class.getResource("/forecast.json").toURI())));

        assertNotNull(jsonString);

        ObjectMapper mapper = new ObjectMapper();

        Forecastdata forecastData = mapper.readValue(jsonString, Forecastdata.class);
        assertNotNull(forecastData);
    }

}
