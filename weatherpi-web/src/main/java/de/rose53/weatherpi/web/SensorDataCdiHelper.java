package de.rose53.weatherpi.web;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.ESensorPlace;
import de.rose53.pi.weatherpi.ESensorType;
import de.rose53.pi.weatherpi.database.Database;
import de.rose53.pi.weatherpi.database.ERange;
import de.rose53.pi.weatherpi.database.SensorDataQueryResult;

// TODO change, if I will get CDI working in JAX RS resources
public class SensorDataCdiHelper {

    static SensorDataCdiHelper instance;

    @Inject
    Logger logger;

    @Inject
    Database database;

    @PostConstruct
    public void init() {
        instance = this;
    }

    public SensorDataQueryResult[] getSensorData(String sensor, String place, String range) {

        ESensorType sensorType = ESensorType.fromString(sensor);
        if (sensorType == null) {
            logger.debug("getSensorData: unknown sensor >{}<",sensor);
            return null;
        }
        try {
            return database.getSensorData(sensorType,ESensorPlace.fromString(place),ERange.fromString(range));
        } catch (SQLException e) {
            logger.error("getSensorData: ",e);
            return new SensorDataQueryResult[0];
        }
    }
}
