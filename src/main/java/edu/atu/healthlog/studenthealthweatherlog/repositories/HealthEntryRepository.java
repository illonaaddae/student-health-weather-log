package edu.atu.healthlog.studenthealthweatherlog.repositories;

import edu.atu.healthlog.studenthealthweatherlog.models.HealthEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HealthEntryRepository {
    private final Connection connection;

    public HealthEntryRepository(Connection connection) {
        this.connection = connection;
    }

    public int save(HealthEntry entry) throws SQLException {
        if (entry == null) throw new IllegalArgumentException("HealthEntry cannot be null");

        String sql = """
            INSERT INTO health_entries (user_id, entry_date, mood_score, sleep_hours, water_intake, exercise, weather_condition, temperature, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entry.getUserId());
            stmt.setDate(2, Date.valueOf(entry.getEntryDate()));
            stmt.setString(3, entry.getMoodScore());
            stmt.setDouble(4, entry.getSleepHours());
            stmt.setDouble(5, entry.getWaterIntake());
            stmt.setString(6, entry.getExercise());
            stmt.setString(7, entry.getWeatherCondition());
            stmt.setDouble(8, entry.getTemperature());
            stmt.setString(9, entry.getNotes());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Inserting health entry failed — no generated key returned");
    }

    public Optional<HealthEntry> findById(int id) throws SQLException {
        if (id < 1) throw new IllegalArgumentException("Entry id must be positive");

        String sql = "SELECT * FROM health_entries WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<HealthEntry> findByUserId(int userId) throws SQLException {
        if (userId < 1) throw new IllegalArgumentException("User id must be positive");

        String sql = "SELECT * FROM health_entries WHERE user_id = ? ORDER BY entry_date DESC, id DESC";
        List<HealthEntry> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    public List<HealthEntry> findAll() throws SQLException {
        List<HealthEntry> results = new ArrayList<>();
        String sql = "SELECT * FROM health_entries";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    public boolean update(HealthEntry entry) throws SQLException {
        if (entry == null) throw new IllegalArgumentException("HealthEntry cannot be null");

        String sql = """
            UPDATE health_entries
            SET user_id = ?, entry_date = ?, mood_score = ?, sleep_hours = ?, water_intake = ?, exercise = ?, weather_condition = ?, temperature = ?, notes = ?
            WHERE id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entry.getUserId());
            stmt.setDate(2, Date.valueOf(entry.getEntryDate()));
            stmt.setString(3, entry.getMoodScore());
            stmt.setDouble(4, entry.getSleepHours());
            stmt.setDouble(5, entry.getWaterIntake());
            stmt.setString(6, entry.getExercise());
            stmt.setString(7, entry.getWeatherCondition());
            stmt.setDouble(8, entry.getTemperature());
            stmt.setString(9, entry.getNotes());
            stmt.setInt(10, entry.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        if (id < 1) throw new IllegalArgumentException("Entry id must be positive");

        String sql = "DELETE FROM health_entries WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private HealthEntry mapRow(ResultSet rs) throws SQLException {
        HealthEntry entry = new HealthEntry(
                rs.getInt("user_id"),
                rs.getDate("entry_date").toLocalDate(),
                rs.getString("mood_score"),
                rs.getDouble("sleep_hours"),
                rs.getDouble("water_intake"),
                rs.getString("exercise"),
                rs.getString("notes")
        );
        entry.setId(rs.getInt("id"));
        entry.setWeatherCondition(rs.getString("weather_condition"));
        entry.setTemperature(rs.getDouble("temperature"));
        return entry;
    }
}
