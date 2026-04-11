package edu.atu.healthlog.studenthealthweatherlog.repositories;

import edu.atu.healthlog.studenthealthweatherlog.models.Correlation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CorrelationRepository {

    private final Connection connection;

    public CorrelationRepository(Connection connection) {
        this.connection = connection;
    }

    // ── save ──────────────────────────────────────────────────────────────────

    public int save(Correlation correlation) throws SQLException {
        if (correlation == null) throw new IllegalArgumentException("Correlation cannot be null");

        String sql = """
            INSERT INTO correlations
                (user_id, health_entry_id, entry_date, weather_condition, temperature, mood_numeric, correlation_score)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, correlation.getUserId());
            stmt.setInt(2, correlation.getHealthEntryId());
            stmt.setDate(3, Date.valueOf(correlation.getEntryDate()));
            stmt.setString(4, correlation.getWeatherCondition());
            stmt.setDouble(5, correlation.getTemperature());
            stmt.setInt(6, correlation.getMoodNumeric());
            stmt.setDouble(7, correlation.getCorrelationScore());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Insert failed — no generated key returned");
    }

    // ── findById ──────────────────────────────────────────────────────────────

    public Optional<Correlation> findById(int id) throws SQLException {
        if (id < 1) throw new IllegalArgumentException("id must be positive");

        String sql = "SELECT * FROM correlations WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    // ── findByUserId ──────────────────────────────────────────────────────────

    public List<Correlation> findByUserId(int userId) throws SQLException {
        if (userId < 1) throw new IllegalArgumentException("userId must be positive");

        String sql = "SELECT * FROM correlations WHERE user_id = ? ORDER BY entry_date DESC";
        List<Correlation> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
        }
        return results;
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    public List<Correlation> findAll() throws SQLException {
        List<Correlation> results = new ArrayList<>();
        String sql = "SELECT * FROM correlations ORDER BY entry_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) results.add(mapRow(rs));
        }
        return results;
    }

    // ── update ────────────────────────────────────────────────────────────────

    public boolean update(Correlation correlation) throws SQLException {
        if (correlation == null) throw new IllegalArgumentException("Correlation cannot be null");

        String sql = """
            UPDATE correlations
            SET user_id = ?, health_entry_id = ?, entry_date = ?,
                weather_condition = ?, temperature = ?, mood_numeric = ?, correlation_score = ?
            WHERE id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, correlation.getUserId());
            stmt.setInt(2, correlation.getHealthEntryId());
            stmt.setDate(3, Date.valueOf(correlation.getEntryDate()));
            stmt.setString(4, correlation.getWeatherCondition());
            stmt.setDouble(5, correlation.getTemperature());
            stmt.setInt(6, correlation.getMoodNumeric());
            stmt.setDouble(7, correlation.getCorrelationScore());
            stmt.setInt(8, correlation.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    // ── delete ────────────────────────────────────────────────────────────────

    public boolean delete(int id) throws SQLException {
        if (id < 1) throw new IllegalArgumentException("id must be positive");

        String sql = "DELETE FROM correlations WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // ── private ───────────────────────────────────────────────────────────────

    private Correlation mapRow(ResultSet rs) throws SQLException {
        Correlation c = new Correlation();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setHealthEntryId(rs.getInt("health_entry_id"));
        c.setEntryDate(rs.getDate("entry_date").toLocalDate());
        c.setWeatherCondition(rs.getString("weather_condition"));
        c.setTemperature(rs.getDouble("temperature"));
        c.setMoodNumeric(rs.getInt("mood_numeric"));
        c.setCorrelationScore(rs.getDouble("correlation_score"));
        return c;
    }
}
