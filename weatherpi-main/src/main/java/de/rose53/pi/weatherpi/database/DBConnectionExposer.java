package de.rose53.pi.weatherpi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;

public class DBConnectionExposer {

	@Produces
	public Connection expose() {

		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			connection = DriverManager.getConnection("jdbc:mysql://localhost/weatherpi?user=weatherpi&password=weatherpi");

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}

	public void closeConnection(@Disposes Connection connection) {

		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
