package edu.atu.healthlog.studenthealthweatherlog.database;

import edu.atu.healthlog.studenthealthweatherlog.models.HealthEntry;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * HealthLogRepository - Handles CRUD operations for health entries in the database.
 */
public class HealthLogRepository {

    public void save(HealthEntry entry) throws SQLException {
        String sql = "INSERT INTO health_entries (user_id, entry_date, mood, sleep_hours, water_intake, exercise, notes, weather_condition, temperature) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, entry.getUserId());
            pstmt.setDate(2, Date.valueOf(entry.getEntryDate()));
            pstmt.setString(3, entry.getMood());
            pstmt.setDouble(4, entry.getSleepHours());
            pstmt.setDouble(5, entry.getWaterIntake());
            pstmt.setString(6, entry.getExercise());
            pstmt.setString(7, entry.getNotes());
            pstmt.setString(8, entry.getWeatherCondition());
            pstmt.setDouble(9, entry.getTemperature());

            pstmt.executeUpdate();
        }
    }

    public List<HealthEntry> getAllByUserId(int userId) throws SQLException {
        List<HealthEntry> entries = new ArrayList<>();
        String sql = "SELECT * FROM health_entries WHERE user_id = ? ORDER BY entry_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HealthEntry entry = new HealthEntry();
                    entry.setId(rs.getInt("id"));
                    entry.setUserId(rs.getInt("user_id"));
                    entry.setEntryDate(rs.getDate("entry_date").toLocalDate());
                    entry.setMood(rs.getString("mood"));
                    entry.setSleepHours(rs.getDouble("sleep_hours"));
                    entry.setWaterIntake(rs.getDouble("water_intake"));
                    entry.setExercise(rs.getString("exercise"));
                    entry.setNotes(rs.getString("notes"));
                    entry.setWeatherCondition(rs.getString("weather_condition"));
                    entry.setTemperature(rs.getDouble("temperature"));
                    entries.add(entry);
                }
            }
        }
        return entries;
    }
}
