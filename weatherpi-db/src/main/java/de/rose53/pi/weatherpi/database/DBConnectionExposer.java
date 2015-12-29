package de.rose53.pi.weatherpi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;

import de.rose53.pi.weatherpi.common.configuration.StringConfiguration;

public class DBConnectionExposer {

    @Inject
    Logger logger;

    @Inject
    @StringConfiguration(key="db.connection",defaultValue="jdbc:mysql://localhost/weatherpi?user=weatherpi&password=weatherpi")
    String connectionUrl;

    @Produces
    public Connection expose() {

        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();

            connection = DriverManager.getConnection(connectionUrl);

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
            logger.error("expose: ",e);
        }
        return connection;
    }

    public void closeConnection(@Disposes Connection connection) {

        try {
            connection.close();
        } catch (SQLException e) {
            logger.error("closeConnection: ",e);
        }

    }
}
