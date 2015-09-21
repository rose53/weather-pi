package de.rose53.pi.weatherpi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.ESensorType;

public class Database {

    static private final String SENSOR_DATA_INSERT = "insert into SENSOR_DATA (TIME,TEMPERATURE,PRESSURE,HUMIDITY,ILLUMINATION) values (SYSDATE(),?,?,?,?)";

    static private final String SENSOR_DATA_TEMPERATURE_GET = "select TIME,TEMPERATURE from SENSOR_DATA where TIME between ? and ? order by TIME";
    static private final String SENSOR_DATA_HUMIDITY_GET = "select TIME,HUMIDITY from SENSOR_DATA where TIME between ? and ? order by TIME";
    static private final String SENSOR_DATA_PRESSURE_GET = "select TIME,HUMIDITY from SENSOR_DATA where TIME between ? and ? order by TIME";
    static private final String SENSOR_DATA_ILLUMINATION_GET = "select TIME,ILLUMINATION from SENSOR_DATA where TIME between ? and ? order by TIME";

    @Inject
    Logger logger;

    @Inject
    Connection connection;


    private PreparedStatement insertStatement;
    private PreparedStatement temperatureGetStatement;
    private PreparedStatement humidityGetStatement;
    private PreparedStatement pressureGetStatement;
    private PreparedStatement illuminationGetStatement;

    @PostConstruct
    public void init() {
        try {
            insertStatement = connection.prepareStatement(SENSOR_DATA_INSERT);
            temperatureGetStatement  = connection.prepareStatement(SENSOR_DATA_TEMPERATURE_GET);
            humidityGetStatement     = connection.prepareStatement(SENSOR_DATA_HUMIDITY_GET);
            pressureGetStatement     = connection.prepareStatement(SENSOR_DATA_PRESSURE_GET);
            illuminationGetStatement = connection.prepareStatement(SENSOR_DATA_ILLUMINATION_GET);
        } catch (SQLException e) {
            logger.error("init:",e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            insertStatement.close();
            temperatureGetStatement.close();
            humidityGetStatement.close();
            pressureGetStatement.close();
            illuminationGetStatement.close();
        } catch (SQLException e) {
            logger.error("destroy:",e);
        }
    }

    public void insertSensorData(double temperature, double pressure, double humidity, double illuminance) throws SQLException {
        insertStatement.setDouble(1, temperature);
        insertStatement.setDouble(2, pressure);
        insertStatement.setDouble(3, humidity);
        insertStatement.setDouble(4, illuminance);

        insertStatement.executeUpdate();
    }

    public List<SensorDataQueryResult> getSensorData(ESensorType sensorType) throws SQLException {

        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);

        PreparedStatement s = null;
        switch (sensorType) {
        case HUMIDITY:
            s = humidityGetStatement;
            break;
        case ILLUMINANCE:
            s = illuminationGetStatement;
            break;
        case PRESSURE:
            s = pressureGetStatement;
            break;
        case TEMPERATURE:
            s = temperatureGetStatement;
            break;
        default:
            break;
        }
        if (s == null) {
            logger.error("getSensorData: no statement found for sensorType = >{}<",sensorType);
            return Collections.emptyList();
        }

        s.setTimestamp(1, Timestamp.valueOf(oneDayAgo));
        s.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

        List<SensorDataQueryResult> retVal = new ArrayList<>();
        try (ResultSet result = s.executeQuery()) {
            while (result.next()) {
                retVal.add(new SensorDataQueryResult(result.getTimestamp(1).toLocalDateTime(),result.getDouble(2)));
            }
        }
        return retVal;
    }
}
