package edu.atu.healthlog.studenthealthweatherlog.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseConnection - Manages MySQL database connection and initialization.
 */
public class DatabaseConnection {
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "healthlog_db";
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
            
            // Seed sample data if table is empty
            seedSampleData(conn);
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            System.err.println("Please ensure MySQL is running and credentials in DatabaseConnection.java are correct.");
        }
    }

    private static void seedSampleData(Connection conn) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM health_entries";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Seeding sample data...");
                String seedSql = "INSERT INTO health_entries (user_id, entry_date, mood, sleep_hours, water_intake, exercise, notes, weather_condition, temperature) VALUES " +
                        "(1, CURDATE(), 'Happy', 8.0, 2.5, 'Running', 'Great day!', 'Sunny', 22.0)," +
                        "(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'Productive', 7.5, 2.0, 'Yoga', 'Felt good.', 'Cloudy', 18.5)," +
                        "(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 'Tired', 6.0, 1.5, 'None', 'Busy at work.', 'Rainy', 15.0)," +
                        "(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 'Energetic', 8.5, 3.0, 'Gym', 'Personal record!', 'Sunny', 25.0)," +
                        "(1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 'Calm', 7.0, 1.8, 'Walking', 'Nice evening walk.', 'Clear', 20.0)," +
                        "(1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 'Stressed', 5.5, 1.2, 'None', 'Deadlines approaching.', 'Overcast', 16.0)," +
                        "(1, DATE_SUB(CURDATE(), INTERVAL 6 DAY), 'Motivated', 7.8, 2.2, 'Cycling', 'Explored new trail.', 'Breezy', 19.0);";
                stmt.executeUpdate(seedSql);
                System.out.println("Sample data seeded.");
            }
        }
    }
}
