package de.rose53.pi.weatherpi.events;

import static org.junit.Assert.*;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SensorEventTest {

    private final String EVENT = "{\"place\":\"ANEMOMETER\",\"sensor\":\"ELTAKO_WS\",\"type\":\"WINDSPEED\",\"windspeed\":0.76}";

    private final String EVENT1 = "{\"place\":\"BIRDHOUSE\",\"sensor\":\"BME280\",\"type\":\"PRESSURE\",\"time\":1502381794}";

    private final String EVENT2 = "{\"place\":\"BIRDHOUSE\"}";

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
    public void testBuildAnemometer() {
        JsonObject object = Json.createReader(new StringReader(EVENT)).readObject();
        assertNotNull(object);
        SensorEvent sensorEvent = SensorEvent.build(object);
        assertNotNull(sensorEvent);
    }

    @Test
    public void testBuildEvent1() {
        JsonObject object = Json.createReader(new StringReader(EVENT1)).readObject();
        assertNotNull(object);
        assertNull(SensorEvent.build(object));
    }

    @Test
    public void testBuildEvent2() {
        JsonObject object = Json.createReader(new StringReader(EVENT2)).readObject();
        assertNotNull(object);
        assertNull(SensorEvent.build(object));
    }

}
