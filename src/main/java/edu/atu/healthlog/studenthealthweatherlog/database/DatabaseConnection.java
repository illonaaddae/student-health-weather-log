package edu.atu.healthlog.studenthealthweatherlog.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseConnection - Manages MySQL database connection and initialization.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/healthlog_db";
    private static final String USER = "root"; // Update with actual DB credentials
    private static final String PASSWORD = "password"; 

    private static Connection connection = null;

    /**
     * Gets the current database connection.
     * Initializes it if it doesn't exist.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Register JDBC driver (not strictly required for modern JDBC but good practice)
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found.", e);
            }
        }
        return connection;
    }

    /**
     * Initializes the database schema.
     */
    public static void initializeDatabase() {
        String createTableSql = "CREATE TABLE IF NOT EXISTS health_entries (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id INT NOT NULL," +
                "entry_date DATE NOT NULL," +
                "mood VARCHAR(50)," +
                "sleep_hours DOUBLE," +
                "water_intake DOUBLE," +
                "exercise VARCHAR(255)," +
                "notes TEXT," +
                "weather_condition VARCHAR(100)," +
                "temperature DOUBLE" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
            System.out.println("Database tables initialized.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            // This might fail if the DB doesn't exist yet, we'd need to create it or assume it exists.
        }
    }
}
