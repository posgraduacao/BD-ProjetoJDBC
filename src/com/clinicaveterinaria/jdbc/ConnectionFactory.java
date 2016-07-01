package com.clinicaveterinaria.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory {
	public Connection getConnection() {
		try {
			return DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/clinicaveterinariadb", "SA", "");
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
}
