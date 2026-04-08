package com.kingsley.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserModelTests {
    private User user;
    private final int VALID_ID = 1;
    private final String VALID_USERNAME = "kwame123";
    private final String VALID_EMAIL = "kwame@gmail.com";
    private final String VALID_PASSWORD = "secret99";
    private final String VALID_CITY = "Accra";

    @BeforeEach
    public void createUser(){
        user = new User(VALID_ID, VALID_USERNAME, VALID_EMAIL, VALID_PASSWORD, VALID_CITY);
    }

    @Test
    void constructor_withValidInputs_createUserSuccessfully(){
        assertNotNull(user);
    }

    @Test
    void getID_afterConstruction_returnCorrectValue(){
        int userID = user.getUserId();
        assertEquals(VALID_ID, userID);
    }

    @Test
    void getUserName_afterConstruction_returnsCorrectValue(){
        String userName = user.getUsername();
        assertEquals(VALID_USERNAME, userName);
    }

    @Test
    void getEmail_afterConstruction_returnCorrectValue(){
        String userEmail = user.getEmail();
        assertEquals(VALID_EMAIL, userEmail);
    }

    @Test
    void getPassword_afterConstruction_returnUserPasswordValue(){
        String userPassword = user.getPassword();
        assertEquals(VALID_PASSWORD, userPassword);
    }

    @Test
    void getCity_afterConstruction_returnUserCityValue(){
        String userCity = user.getCity();
        assertEquals(VALID_CITY, userCity);
    }

    @Test
    void setUsername_withNull_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class,() -> user.setUserName(null));
    }

    @Test
    void setUsername_withEmptyString_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setUserName(""));
    }

    @Test
    void setUsername_withOnlyWhiteSpace_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setUserName("    "));
    }

    @Test
    void setUsername_withRightValue_setsSuccessfully(){
        user.setUserName("testUsername");
        assertEquals("testUsername", user.getUsername());
    }

    @Test
    void setEmail_withNull_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setEmail(null));
    }

    @Test
    void setEmail_withEmptyString_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setEmail(""));
    }

    @Test
    void setEmail_withWhiteSpaceOnly_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setEmail("   "));
    }

    @Test
    void setEmail_withNoAtSymbol_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setEmail("usergmail.com"));
    }

    @Test
    void setEmail_withNoDomain_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setEmail("user@"));
    }

    @Test
    void setEmail_withNoUserPart_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setEmail("@gmail.com"));
    }

    @Test
    void setEmail_withRightValue_successfullySet(){
        user.setEmail("user@gmail.com");
        assertEquals("user@gmail.com", user.getEmail());
    }

    @Test
    void setPassword_withNull_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setPassword(null));
    }

    @Test
    void setPassword_withEmptyValue_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setPassword(""));
    }

    @Test
    void setPassword_withWhitespace_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setPassword("    "));
    }

    @Test
    void setPassword_withValueLengthLessThanEight_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setPassword("ascsdf"));
    }

    @Test
    void setPassword_withRightValue_successfullySet(){
        user.setPassword("newUserPassword");
        assertEquals("newUserPassword", user.getPassword());
    }

    @Test
    void setCity_withNull_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setCity(null));
    }

    @Test
    void setCity_withEmptyValue_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setCity(""));
    }

    @Test
    void setCity_withWhitespace_throwsIllegalArgumentException(){
        assertThrows(IllegalArgumentException.class, () -> user.setCity("    "));
    }

    @Test
    void setCity_withRightValue_successfullySet(){
        user.setCity("newUserPassword");
        assertEquals("newUserPassword", user.getCity());
    }

    @Test
    void equals_twoUsersWithSameId_returnsTrue() {
        User userA = new User(1, "kwame123", "kwame@gmail.com", "Secret99", "Accra");
        User userB = new User(1, "different", "other@gmail.com", "Secret99", "Tema");
        assertEquals(userA, userB);
    }@Test
    void equals_twoUsersWithDifferentId_returnsFalse() {
        User userA = new User(1, "kwame123", "kwame@gmail.com", "Secret99", "Accra");
        User userB = new User(2, "kwame123", "kwame@gmail.com", "Secret99", "Tema");
        assertNotEquals(userA, userB);
    }

    @Test
    void hashCode_twoUsersWithSameId_returnsSameHash() {
        User userA = new User(1, "kwame123", "kwame@gmail.com", "Secret99", "Accra");
        User userB = new User(1, "different", "other@gmail.com", "Secret99", "Tema");
        assertEquals(userA.hashCode(), userB.hashCode());
    }
}