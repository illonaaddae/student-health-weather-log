package edu.atu.healthlog.studenthealthweatherlog.models;

public class User {
    private int id;
    private String username;
    protected String email;
    private String password;
    private String city;

    public User(int validId, String validUsername, String validEmail, String validPassword, String validCity) {
        this.id = validId;
        this.username = validUsername;
        this.email = validEmail;
        this.password = validPassword;
        this.city = validCity;
    }

    public String getUsername() {
        return this.username;
    }

    public int getUserId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getCity() {
        return this.city;
    }

    public void setUserName(String userName) throws IllegalArgumentException{
        if (userName == null)
            throw new IllegalArgumentException("User's username cannot be null");
        if (userName.isBlank())
            throw new IllegalArgumentException("Username cannot be empty");
        this.username = userName;
    }

    public void setEmail(String email) throws IllegalArgumentException {
        if (email == null)
            throw new IllegalArgumentException("Email cannot be null");
        if (email.isBlank())
            throw new IllegalArgumentException("Email cannot be empty");
        if (!email.contains("@"))
            throw new IllegalArgumentException("Email is missing the @ symbol");
        if (email.split("@").length < 2)
            throw new IllegalArgumentException("Email does not contain domain part");
        if (email.substring(0, email.indexOf("@")).isBlank())
            throw new IllegalArgumentException("Email is missing user part");
        this.email = email;
    }

    public void setPassword(String password) {
        if (password == null)
            throw new IllegalArgumentException("Password must be provided");
        if (password.isBlank())
            throw new IllegalArgumentException("Password cannot be empty");
        if (password.length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        this.password = password;
    }

    public void setCity(String city) {
        if (city == null)
            throw new IllegalArgumentException("City must be provided");
        if (city.isBlank())
            throw new IllegalArgumentException("City cannot be empty");
        this.city = city;
    }

    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        User other = (User) obj;
        return this.id == other.id;
    }

    public int hashCode() {
        return Integer.hashCode(this.id);
    }
}
