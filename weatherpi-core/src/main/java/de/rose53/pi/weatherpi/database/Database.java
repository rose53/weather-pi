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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.ESensorPlace;
import de.rose53.pi.weatherpi.ESensorType;

@ApplicationScoped
public class Database {

    static private final String SENSOR_DATA_INSERT = "insert into SENSOR_DATA (TIME,TEMPERATURE,PRESSURE,HUMIDITY,ILLUMINATION,TEMPERATURE_OUT,HUMIDITY_OUT) values (SYSDATE(),?,?,?,?,?,?)";

    static private final String SENSOR_DATA_TEMPERATURE_GET = "select TIME,TEMPERATURE from SENSOR_DATA where TIME between ? and ? order by TIME DESC";
    static private final String SENSOR_DATA_HUMIDITY_GET = "select TIME,HUMIDITY from SENSOR_DATA where TIME between ? and ? order by TIME DESC";
    static private final String SENSOR_DATA_PRESSURE_GET = "select TIME,PRESSURE from SENSOR_DATA where TIME between ? and ? order by TIME DESC";
    static private final String SENSOR_DATA_ILLUMINATION_GET = "select TIME,ILLUMINATION from SENSOR_DATA where TIME between ? and ? order by TIME DESC";
    static private final String SENSOR_DATA_TEMPERATURE_OUT_GET = "select TIME,TEMPERATURE_OUT from SENSOR_DATA where TIME between ? and ? order by TIME DESC";
    static private final String SENSOR_DATA_HUMIDITY_OUT_GET = "select TIME,HUMIDITY_OUT from SENSOR_DATA where TIME between ? and ? order by TIME DESC";

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
        } catch (SQLException e) {
            logger.error("destroy:",e);
        }
    }

    public void insertSensorData(RowData rowData) throws SQLException {
        if (rowData == null) {
            logger.debug("insertSensorData: not data given.");
            return;
        }
        insertStatement.setDouble(1, rowData.getTemperatureIndoor());
        insertStatement.setDouble(2, rowData.getPressureIndoor());
        insertStatement.setDouble(3, rowData.getHumidityIndoor());
        insertStatement.setDouble(4, rowData.getIlluminanceIndoor());
        insertStatement.setDouble(5, rowData.getTemperatureOutdoor());
        insertStatement.setDouble(6, rowData.getHumidityOutdoor());

        insertStatement.executeUpdate();
    }

    public List<SensorDataQueryResult> getSensorData(ESensorType sensorType, ESensorPlace place, ERange range) throws SQLException {

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
            }
            break;
        default:
            break;
        }
        if (s == null) {
            logger.error("getSensorData: no statement found for sensorType = >{}<",sensorType);
            return Collections.emptyList();
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
        return retVal;
    }
}
