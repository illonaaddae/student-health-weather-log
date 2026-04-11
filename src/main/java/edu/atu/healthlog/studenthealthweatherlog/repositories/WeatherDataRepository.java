package edu.atu.healthlog.studenthealthweatherlog.repositories;

import edu.atu.healthlog.studenthealthweatherlog.models.WeatherData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WeatherDataRepository {
    private final Connection connection;

    public WeatherDataRepository(Connection connection) {
        this.connection = connection;
    }

    public int save(WeatherData weatherData) throws SQLException {
        if (weatherData == null) throw new IllegalArgumentException("WeatherData cannot be null");

        String sql = """
            INSERT INTO weather_data (date, temperature, humidity, `condition`, uv_index, wind_speed, city)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(weatherData.getDate()));
            stmt.setDouble(2, weatherData.getTemperature());
            stmt.setDouble(3, weatherData.getHumidity());
            stmt.setString(4, weatherData.getCondition());
            stmt.setDouble(5, weatherData.getUvIndex());
            stmt.setDouble(6, weatherData.getWindSpeed());
            stmt.setString(7, weatherData.getCity());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Inserting weather data failed — no generated key returned");
    }

    public Optional<WeatherData> findById(int id) throws SQLException {
        if (id < 1) throw new IllegalArgumentException("Weather data id must be positive");

        String sql = "SELECT * FROM weather_data WHERE weather_id = ?";
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

    public List<WeatherData> findByCity(String city) throws SQLException {
        if (city == null || city.isBlank()) throw new IllegalArgumentException("City cannot be null or blank");

        String sql = "SELECT * FROM weather_data WHERE city = ?";
        List<WeatherData> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, city);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    public List<WeatherData> findAll() throws SQLException {
        List<WeatherData> results = new ArrayList<>();
        String sql = "SELECT * FROM weather_data";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    public boolean update(WeatherData weatherData) throws SQLException {
        if (weatherData == null) throw new IllegalArgumentException("WeatherData cannot be null");

        String sql = """
            UPDATE weather_data
            SET date = ?, temperature = ?, humidity = ?, `condition` = ?, uv_index = ?, wind_speed = ?, city = ?
            WHERE weather_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(weatherData.getDate()));
            stmt.setDouble(2, weatherData.getTemperature());
            stmt.setDouble(3, weatherData.getHumidity());
            stmt.setString(4, weatherData.getCondition());
            stmt.setDouble(5, weatherData.getUvIndex());
            stmt.setDouble(6, weatherData.getWindSpeed());
            stmt.setString(7, weatherData.getCity());
            stmt.setInt(8, weatherData.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        if (id < 1) throw new IllegalArgumentException("Weather data id must be positive");

        String sql = "DELETE FROM weather_data WHERE weather_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private WeatherData mapRow(ResultSet rs) throws SQLException {
        WeatherData weatherData = new WeatherData(
                rs.getInt("weather_id"),
                rs.getDate("date").toLocalDate(),
                rs.getString("city")
        );
        weatherData.setTemperature(rs.getDouble("temperature"));
        weatherData.setHumidity(rs.getDouble("humidity"));
        weatherData.setCondition(rs.getString("condition"));
        weatherData.setUvIndex(rs.getDouble("uv_index"));
        weatherData.setWindSpeed(rs.getDouble("wind_speed"));
        return weatherData;
    }
}
