package de.rose53.pi.weatherpi.events;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SensorEventTest {

    private final ObjectMapper mapper = new ObjectMapper();


    private final String message = "{\"place\":\"BIRDHOUSE\",\"sensor\":\"BME280\",\"type\":\"PRESSURE\",\"time\":1469354818,\"pressure\":1.022060e3}";

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
    public void test() throws JsonParseException, JsonMappingException, IOException {
        SensorEvent event = mapper.readValue(message, SensorEvent.class);
        assertNotNull(event);
    }

}
