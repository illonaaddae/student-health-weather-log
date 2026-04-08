package com.kingsley.repositories;

import com.kingsley.models.HealthEntry;

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
            INSERT INTO health_entries (user_id, weather_id, date, mood_score, activity_type, activity_duration, energy_level, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, entry.getUserId());
            stmt.setInt(2, entry.getWeatherId());
            stmt.setDate(3, Date.valueOf(entry.getDate()));
            stmt.setInt(4, entry.getMoodScore());
            stmt.setString(5, entry.getActivityType());
            stmt.setDouble(6, entry.getActivityDuration());
            stmt.setInt(7, entry.getEnergyLevel());
            stmt.setString(8, entry.getNotes());

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

        String sql = "SELECT * FROM health_entries WHERE entry_id = ?";
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

        String sql = "SELECT * FROM health_entries WHERE user_id = ?";
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
            SET user_id = ?, weather_id = ?, date = ?, mood_score = ?, activity_type = ?, activity_duration = ?, energy_level = ?, notes = ?
            WHERE entry_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, entry.getUserId());
            stmt.setInt(2, entry.getWeatherId());
            stmt.setDate(3, Date.valueOf(entry.getDate()));
            stmt.setInt(4, entry.getMoodScore());
            stmt.setString(5, entry.getActivityType());
            stmt.setDouble(6, entry.getActivityDuration());
            stmt.setInt(7, entry.getEnergyLevel());
            stmt.setString(8, entry.getNotes());
            stmt.setInt(9, entry.getEntryId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        if (id < 1) throw new IllegalArgumentException("Entry id must be positive");

        String sql = "DELETE FROM health_entries WHERE entry_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private HealthEntry mapRow(ResultSet rs) throws SQLException {
        return new HealthEntry(
                rs.getInt("entry_id"),
                rs.getInt("user_id"),
                rs.getInt("weather_id"),
                rs.getDate("date").toLocalDate(),
                rs.getInt("mood_score"),
                rs.getString("activity_type"),
                rs.getDouble("activity_duration"),
                rs.getInt("energy_level"),
                rs.getString("notes")
        );
    }
}
