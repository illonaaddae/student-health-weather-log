package edu.atu.healthlog.studenthealthweatherlog.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseConnection - Manages MySQL database connection and initialization.
 */
public class DatabaseConnection {
    private static final String BASE_URL = "jdbc:mysql://localhost:3307/";
    private static final String DB_NAME = "healthlog_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private static Connection connection = null;
    private static boolean isUsingMock = false;

    /**
     * Gets the current database connection.
     * Initializes it if it doesn't exist.
     */
    public static Connection getConnection() throws SQLException {
        if (isUsingMock) {
            throw new SQLException("Database is in mock mode due to previous connection failure.");
        }
        if (connection == null || connection.isClosed()) {
            try {
                // Register JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // First attempt to connect to the database
                try {
                    connection = DriverManager.getConnection(BASE_URL + DB_NAME, USER, PASSWORD);
                } catch (SQLException e) {
                    // If connection fails, try connecting to server without DB and create it
                    System.out.println("Database " + DB_NAME + " not found or connection failed. Attempting to create...");
                    try (Connection serverConn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
                         Statement stmt = serverConn.createStatement()) {
                        stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
                        System.out.println("Database " + DB_NAME + " created successfully.");
                    }
                    // Try connecting again after creation
                    connection = DriverManager.getConnection(BASE_URL + DB_NAME, USER, PASSWORD);
                }
            } catch (ClassNotFoundException e) {
                isUsingMock = true;
                throw new SQLException("MySQL JDBC Driver not found.", e);
            }
        }
        return connection;
    }

    public static boolean isMockMode() {
        return isUsingMock;
    }

    /**
     * Initializes the database schema (creates tables if they don't exist).
     */
    public static void initializeDatabase() {
        String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id  INT AUTO_INCREMENT PRIMARY KEY," +
                "username VARCHAR(100) NOT NULL," +
                "email    VARCHAR(255) NOT NULL UNIQUE," +
                "password VARCHAR(255) NOT NULL," +
                "city     VARCHAR(100) NOT NULL DEFAULT 'London'" +
                ");";

        String createEntries = "CREATE TABLE IF NOT EXISTS health_entries (" +
                "id                INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id           INT          NOT NULL," +
                "entry_date        DATE         NOT NULL," +
                "mood_score        VARCHAR(50)," +
                "sleep_hours       DOUBLE," +
                "water_intake      DOUBLE," +
                "exercise          VARCHAR(255)," +
                "notes             TEXT," +
                "weather_condition VARCHAR(100)," +
                "temperature       DOUBLE," +
                "FOREIGN KEY (user_id) REFERENCES users(user_id)" +
                ");";

        String createWeatherData = "CREATE TABLE IF NOT EXISTS weather_data (" +
                "weather_id  INT AUTO_INCREMENT PRIMARY KEY," +
                "date        DATE         NOT NULL," +
                "temperature DOUBLE       NOT NULL," +
                "humidity    DOUBLE       NOT NULL," +
                "`condition` VARCHAR(100) NOT NULL," +
                "uv_index    DOUBLE       NOT NULL," +
                "wind_speed  DOUBLE       NOT NULL," +
                "city        VARCHAR(100) NOT NULL" +
                ");";

        String createCorrelations = "CREATE TABLE IF NOT EXISTS correlations (" +
                "id                INT AUTO_INCREMENT PRIMARY KEY," +
                "user_id           INT          NOT NULL," +
                "health_entry_id   INT          NOT NULL," +
                "entry_date        DATE         NOT NULL," +
                "weather_condition VARCHAR(100) NOT NULL," +
                "temperature       DOUBLE       NOT NULL DEFAULT 0.0," +
                "mood_numeric      INT          NOT NULL," +
                "correlation_score DOUBLE       NOT NULL" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(createEntries);
            stmt.execute(createWeatherData);
            stmt.execute(createCorrelations);
            System.out.println("Database tables initialized.");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            System.err.println("Ensure the Docker container is running: docker compose up -d");
            isUsingMock = true;
        }
    }
}
