package de.rose53.pi.weatherpi.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;

@Dependent
public class Database {

    static private final String SENSOR_DATA_INSERT = "insert into SENSOR_DATA (TIME,TEMPERATURE,PRESSURE,HUMIDITY,ILLUMINATION,TEMPERATURE_OUT,HUMIDITY_OUT,TEMPERATURE_BIRD,HUMIDITY_BIRD) values (SYSDATE(),?,?,?,?,?,?,?,?)";

    static private final String SENSOR_DATA_TEMPERATURE_GET = "select TIME,TEMPERATURE from SENSOR_DATA where TIME between ? and ? order by TIME DESC";
    static private final String SENSOR_DATA_HUMIDITY_GET = "select TIME,HUMIDITY from SENSOR_DATA where TIME between ? and ? order by TIME DESC";
    static private final String SENSOR_DATA_PRESSURE_GET = "select TIME,PRESSURE from SENSOR_DATA where TIME between ? and ? order by TIME DESC";
    static private final String SENSOR_DATA_ILLUMINATION_GET = "select TIME,ILLUMINATION from SENSOR_DATA where TIME between ? and ? order by TIME DESC";
    static private final String SENSOR_DATA_TEMPERATURE_OUT_GET = "select TIME,TEMPERATURE_OUT from SENSOR_DATA where TIME between ? and ? order by TIME DESC";
    static private final String SENSOR_DATA_HUMIDITY_OUT_GET = "select TIME,HUMIDITY_OUT from SENSOR_DATA where TIME between ? and ? order by TIME DESC";
    static private final String SENSOR_DATA_TEMPERATURE_BIRD_GET = "select TIME,TEMPERATURE_BIRD from SENSOR_DATA where TIME between ? and ? order by TIME DESC";
    static private final String SENSOR_DATA_HUMIDITY_BIRD_GET = "select TIME,HUMIDITY_BIRD from SENSOR_DATA where TIME between ? and ? order by TIME DESC";

    @Inject
    Logger logger;

    @Inject
    Connection connection;


    private PreparedStatement insertStatement;
    private PreparedStatement temperatureGetStatement;
    private PreparedStatement humidityGetStatement;
    private PreparedStatement pressureGetStatement;
    private PreparedStatement illuminationGetStatement;
    private PreparedStatement temperatureOutGetStatement;
    private PreparedStatement humidityOutGetStatement;
    private PreparedStatement temperatureBirdGetStatement;
    private PreparedStatement humidityBirdGetStatement;

    @PostConstruct
    public void init() {
        try {
            insertStatement = connection.prepareStatement(SENSOR_DATA_INSERT);
            temperatureGetStatement     = connection.prepareStatement(SENSOR_DATA_TEMPERATURE_GET);
            humidityGetStatement        = connection.prepareStatement(SENSOR_DATA_HUMIDITY_GET);
            pressureGetStatement        = connection.prepareStatement(SENSOR_DATA_PRESSURE_GET);
            illuminationGetStatement    = connection.prepareStatement(SENSOR_DATA_ILLUMINATION_GET);
            temperatureOutGetStatement  = connection.prepareStatement(SENSOR_DATA_TEMPERATURE_OUT_GET);
            humidityOutGetStatement     = connection.prepareStatement(SENSOR_DATA_HUMIDITY_OUT_GET);
            temperatureBirdGetStatement = connection.prepareStatement(SENSOR_DATA_TEMPERATURE_BIRD_GET);
            humidityBirdGetStatement    = connection.prepareStatement(SENSOR_DATA_HUMIDITY_BIRD_GET);
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
            temperatureOutGetStatement.close();
            humidityOutGetStatement.close();
            temperatureBirdGetStatement.close();
            humidityBirdGetStatement.close();
        } catch (SQLException e) {
            logger.error("destroy:",e);
        }
    }

    public void insertSensorData(RowData rowData) throws SQLException {
        if (rowData == null) {
            logger.debug("insertSensorData: not data given.");
            return;
        }
        if (!rowData.isValid()) {
            logger.debug("insertSensorData: not valid data given.");
            return;
        }
        insertStatement.setDouble(1, rowData.getTemperatureIndoor());
        insertStatement.setDouble(2, rowData.getPressureIndoor());
        insertStatement.setDouble(3, rowData.getHumidityIndoor());
        insertStatement.setDouble(4, rowData.getIlluminanceIndoor());
        insertStatement.setDouble(5, rowData.getTemperatureOutdoor());
        insertStatement.setDouble(6, rowData.getHumidityOutdoor());
        insertStatement.setDouble(7, rowData.getTemperatureBirdhouse());
        insertStatement.setDouble(8, rowData.getHumidityBirdhouse());

        insertStatement.executeUpdate();
    }

    public synchronized SensorDataQueryResult[] getSensorData(ESensorType sensorType, ESensorPlace place, ERange range) throws SQLException {

        if (range == null) {
            range = ERange.ACTUAL;
        }

        PreparedStatement s = null;
        switch (sensorType) {
        case HUMIDITY:
            switch (place) {
            case INDOOR:
                s = humidityGetStatement;
                break;
            case OUTDOOR:
                s = humidityOutGetStatement;
                break;
            case BIRDHOUSE:
                s = humidityBirdGetStatement;
                break;
            default:
                break;
            }
            break;
        case ILLUMINANCE:
            s = illuminationGetStatement;
            break;
        case PRESSURE:
            s = pressureGetStatement;
            break;
        case TEMPERATURE:
            switch (place) {
            case INDOOR:
                s = temperatureGetStatement;
                break;
            case OUTDOOR:
                s = temperatureOutGetStatement;
                break;
            case BIRDHOUSE:
                s = temperatureBirdGetStatement;
                break;
            default:
                break;
            }
            break;
        default:
            break;
        }
        if (s == null) {
            logger.error("getSensorData: no statement found for sensorType = >{}<",sensorType);
            return new SensorDataQueryResult[0];
        }

        s.setTimestamp(1, Timestamp.valueOf(range.getPastTime()));
        s.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));

        List<SensorDataQueryResult> retVal = new ArrayList<>();
        try (ResultSet result = s.executeQuery()) {
            while (result.next()) {
                retVal.add(new SensorDataQueryResult(result.getTimestamp(1).toLocalDateTime(),result.getDouble(2)));
                if (ERange.ACTUAL == range) {
                    // we only use the first value
                    break;
                }
            }
        }
        return retVal.toArray(new SensorDataQueryResult[retVal.size()]);
    }
}
