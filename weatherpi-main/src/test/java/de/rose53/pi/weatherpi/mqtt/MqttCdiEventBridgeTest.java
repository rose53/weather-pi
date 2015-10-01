package de.rose53.pi.weatherpi.mqtt;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.rose53.pi.weatherpi.ESensorPlace;
import de.rose53.pi.weatherpi.ESensorType;
import de.rose53.pi.weatherpi.events.SensorEvent;
import de.rose53.pi.weatherpi.events.TemperatureEvent;

public class MqttCdiEventBridgeTest {

    @Test
    public void testStringToJson() throws JsonParseException, JsonMappingException, IOException {

        String jsonString = "{\"sensor\":\"DHT22\",\"type\":\"TEMPERATURE\",\"place\":\"OUTDOOR\",\"accuracy\":0.5,\"temperature\":21.5}";

        ObjectMapper mapper = new ObjectMapper();

        SensorEvent event = mapper.readValue(jsonString, SensorEvent.class);

        assertNotNull(event);
        assertTrue(event instanceof TemperatureEvent);
        TemperatureEvent temeratureEvent = (TemperatureEvent)event;
        assertEquals("DHT22", temeratureEvent.getSensor());
        assertEquals(ESensorType.TEMPERATURE, temeratureEvent.getType());
        assertEquals(ESensorPlace.OUTDOOR, temeratureEvent.getPlace());
        assertEquals(0.5, temeratureEvent.getAccuracy(),0.1);
        assertEquals(21.5, temeratureEvent.getTemperature(),0.1);
    }

}
