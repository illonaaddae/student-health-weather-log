package edu.atu.healthlog.studenthealthweatherlog;

import java.util.prefs.Preferences;

/**
 * UserPreferences - stores lightweight app settings.
 */
public final class UserPreferences {
    private static final Preferences PREFS = Preferences.userNodeForPackage(UserPreferences.class);
    private static final String KEY_CITY = "weather.city";
    private static final String DEFAULT_CITY = "";
    private static final String KEY_PAGE_SIZE = "history.pageSize";
    private static final String KEY_USER_NAME = "user.name";
    private static final String KEY_USER_EMAIL = "user.email";
    private static final String KEY_DARK_THEME = "ui.darkTheme";
    private static final String KEY_PROFILE_PICTURE = "user.profilePicturePath";

    private UserPreferences() {
    }

    public static String getCity() {
        return PREFS.get(KEY_CITY, DEFAULT_CITY);
    }

    public static void setCity(String city) {
        if (city == null || city.isBlank()) {
            PREFS.remove(KEY_CITY);
            return;
        }
        PREFS.put(KEY_CITY, city.trim());
    }

    public static int getHistoryPageSize() {
        return PREFS.getInt(KEY_PAGE_SIZE, 10);
    }

    public static void setHistoryPageSize(int pageSize) {
        if (pageSize <= 0) {
            return;
        }
        PREFS.putInt(KEY_PAGE_SIZE, pageSize);
    }

    public static String getUserName() {
        return PREFS.get(KEY_USER_NAME, "Student");
    }

    public static void setUserName(String name) {
        if (name == null || name.isBlank()) {
            return;
        }
        PREFS.put(KEY_USER_NAME, name.trim());
    }

    public static String getUserEmail() {
        return PREFS.get(KEY_USER_EMAIL, "");
    }

    public static void setUserEmail(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        PREFS.put(KEY_USER_EMAIL, email.trim());
    }

    public static boolean isDarkThemeEnabled() {
        return PREFS.getBoolean(KEY_DARK_THEME, false);
    }

    public static void setDarkThemeEnabled(boolean enabled) {
        PREFS.putBoolean(KEY_DARK_THEME, enabled);
    }

    public static String getProfilePicturePath() {
        return PREFS.get(KEY_PROFILE_PICTURE, null);
    }

    public static void setProfilePicturePath(String path) {
        if (path == null || path.isBlank()) {
            PREFS.remove(KEY_PROFILE_PICTURE);
        } else {
            PREFS.put(KEY_PROFILE_PICTURE, path.trim());
        }
    }

}

