package com.kingsley.repositories;


import com.kingsley.models.User;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserRepository integration tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTest {
    private static Connection connection;
    private UserRepository userRepository;

    @BeforeAll
    static void setUpDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
    }

    @BeforeEach
    void setUp() throws SQLException {
        connection.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS users (
                user_id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE,
                email VARCHAR(100) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                city VARCHAR(100) NOT NULL
            )
        """);
        userRepository = new UserRepository(connection);
    }

    @Test
    @Order(1)
    void save_passNull_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> userRepository.save(null));
    }

    @Test
    @Order(2)
    void save_validUserObject_insertsInDatabaseSuccessfully() throws SQLException{
        User user = buildValidUser();
        int generatedId = userRepository.save(user);
        assertTrue(generatedId > 0);
    }

    @Test
    @Order(3)
    void save_withValidUser_persistsDataRetrievableById() throws SQLException {
        User user = buildValidUser();
        int generatedId = userRepository.save(user);

        Optional<User> retrievedUser = userRepository.findById(generatedId);

        assertTrue(retrievedUser.isPresent());
        assertEquals("kwame123", retrievedUser.get().getUsername());
        assertEquals("kwame@gmail.com", retrievedUser.get().getEmail());
        assertEquals("Accra", retrievedUser.get().getCity());
    }

    @Test
    @Order(4)
    void save_withDuplicateUsername_throwsSQLException() throws SQLException {
        userRepository.save(buildValidUser());

        User duplicateUsername = new User(0, "kwame123", "different@gmail.com", "Secret99", "Tema");
        assertThrows(SQLException.class, () -> userRepository.save(duplicateUsername));
    }

    @Test
    @Order(5)
    void save_withDuplicateEmail_throwsSqlException() throws SQLException {
        userRepository.save(buildValidUser());

        User duplicateEmail = new User(0, "differentUser", "kwame@gmail.com", "Secret99", "Tema");
        assertThrows(SQLException.class, () -> userRepository.save(duplicateEmail));
    }


    @Test
    @Order(6)
    void findById_withExistingId_returnsCorrectUser() throws SQLException {
        int generatedId = userRepository.save(buildValidUser());

        Optional<User> result = userRepository.findById(generatedId);

        assertTrue(result.isPresent());
        assertEquals(generatedId, result.get().getUserId());
    }

    @Test
    @Order(7)
    void findById_withNonExistentId_returnsEmptyOptional() throws SQLException{
        Optional<User> result = userRepository.findById(999);
        assertFalse(result.isPresent());
    }

    @Test
    @Order(8)
    void findById_withZeroId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userRepository.findById(0));
    }

    @Test
    @Order(9)
    void findById_withNegativeId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userRepository.findById(-1));
    }

    @Test
    @Order(10)
    @DisplayName("findByUsername with existing username returns correct user")
    void findByUsername_withExistingUsername_returnsCorrectUser() throws SQLException {
        userRepository.save(buildValidUser());

        Optional<User> result = userRepository.findByUsername("kwame123");

        assertTrue(result.isPresent());
        assertEquals("kwame123", result.get().getUsername());
    }

    @Test
    @Order(11)
    void findByUsername_withNonExistentUsername_returnsEmptyOptional() throws SQLException {
        Optional<User> result = userRepository.findByUsername("nobody");
        assertFalse(result.isPresent());
    }

    @Test
    @Order(12)
    void findByUsername_withNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userRepository.findByUsername(null));
    }

    @Test
    @Order(13)
    void findByUsername_withBlank_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userRepository.findByUsername("   "));
    }

    @Test
    @Order(14)
    void findAll_onEmptyTable_returnsEmptyList() throws SQLException {
        List<User> result = userRepository.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @Order(15)
    void findAll_withMultipleUsers_returnsAllUsers() throws SQLException {
        userRepository.save(new User(0, "kwame123", "kwame@gmail.com",   "Secret99", "Accra"));
        userRepository.save(new User(0, "ama_serwaa", "ama@gmail.com",   "Secret99", "Kumasi"));
        userRepository.save(new User(0, "kofi99",   "kofi@gmail.com",    "Secret99", "Tema"));

        List<User> result = userRepository.findAll();

        // Exactly 3 users were inserted — list must contain exactly 3
        assertEquals(3, result.size());
    }

    @Test
    @Order(16)
    void update_withExistingUser_returnsTrueAndPersistsChanges() throws SQLException {
        int generatedId = userRepository.save(buildValidUser());

        Optional<User> saved = userRepository.findById(generatedId);
        assertTrue(saved.isPresent());

        User toUpdate = saved.get();
        toUpdate.setCity("Kumasi");

        boolean result = userRepository.update(toUpdate);

        assertTrue(result);

        Optional<User> updated = userRepository.findById(generatedId);
        assertTrue(updated.isPresent());
        assertEquals("Kumasi", updated.get().getCity());
    }

    @Test
    @Order(17)
    void update_withNonExistentUser_returnsFalse() throws SQLException {
        User ghost = new User(999, "ghost", "ghost@gmail.com", "Secret99", "Nowhere");
        boolean result = userRepository.update(ghost);
        assertFalse(result);
    }

    @Test
    @Order(18)
    void update_withNullUser_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userRepository.update(null));
    }

    @Test
    @Order(19)
    void delete_withExistingId_returnsTrueAndRemovesUser() throws SQLException {
        // Save, delete, then try to find — the user must no longer exist.
        int generatedId = userRepository.save(buildValidUser());

        boolean result = userRepository.delete(generatedId);

        assertTrue(result);

        Optional<User> afterDelete = userRepository.findById(generatedId);
        assertFalse(afterDelete.isPresent());
    }

    @Order(20)
    void delete_withNonExistentId_returnsFalse() throws SQLException {
        boolean result = userRepository.delete(999);
        assertFalse(result);
    }

    @Test
    @Order(21)
    void delete_withZeroId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userRepository.delete(0));
    }

    @Test
    @Order(22)
    void delete_withNegativeId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> userRepository.delete(-1));
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.createStatement().execute("DROP ALL OBJECTS");
    }

    @AfterAll
    static void closeDatabase() throws SQLException {
        connection.close();
    }

    private User buildValidUser(){
        return new User(0, "kwame123", "kwame@gmail.com", "Secret99", "Accra");
    }
}
