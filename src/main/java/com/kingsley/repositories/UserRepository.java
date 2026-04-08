package com.kingsley.repositories;

import com.kingsley.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private final Connection connection;

    public UserRepository(Connection connection){
        this.connection = connection;
    }

    public int save(User user) throws SQLException{
        if (user == null) throw new IllegalArgumentException("User cannot be null");

        String sql = """
            INSERT INTO users (username, email, password, city)
            VALUES (?, ?, ?, ?)
        """;

        try(PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getCity());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()){
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Inserting user failed — no generated key returned");
    }

    public Optional<User> findById(int generatedId) throws SQLException {
        if (generatedId < 1) throw new IllegalArgumentException("User id must be positive");
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (PreparedStatement stnt = connection.prepareStatement(sql)){
            stnt.setInt(1, generatedId);

            try (ResultSet rs = stnt.executeQuery()){
                if (rs.next()){
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getNString("username"),
                rs.getNString("email"),
                rs.getNString("password"),
                rs.getNString("city")
        );
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username cannot be null");

        String sql = "SELECT * FROM users WHERE username = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);

            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()){
                    users.add(mapRow(rs));
                }
            }
        }

        return users;
    }

    public boolean update(User user) throws SQLException {
        if (user == null) throw new IllegalArgumentException("User cannot be null");

        String sql = """
            UPDATE users
            SET username = ?, email = ?, password = ?, city = ?
            WHERE user_id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getCity());
            stmt.setInt(5, user.getUserId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean delete(int userId) throws SQLException {
        if (userId < 1) throw new IllegalArgumentException("User's id must be positive");

        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, userId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
