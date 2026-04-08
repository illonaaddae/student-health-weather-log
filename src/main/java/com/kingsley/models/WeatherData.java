package com.kingsley.models;

import java.time.LocalDate;
import java.util.Objects;

public class WeatherData {
    private int id;
    private LocalDate date;
    private double temperature;
    private double humidity;
    private String condition;
    private double uvIndex;
    private double windSpeed;
    private String city;

    private final double MIN_TEMPERATURE = -90.0;
    private final double MAX_TEMPERATURE = 60.0;


    public WeatherData(int weatherID, LocalDate date, String city){
        this.id = weatherID;
        this.date = date;
        this.city = city;
    }

    public int getId() {
        return this.id;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public String getCity() {
        return this.city;
    }

    public void setDate(LocalDate date) {
        if (date == null) throw new IllegalArgumentException("Date cannot be null");
        if (date.isAfter(LocalDate.now())) throw  new IllegalArgumentException("Date cannot be in the future");
        this.date = date;
    }

    public void setTemperature(double temperature) {
        if (temperature < -90.0) throw new IllegalArgumentException("Temperature cannot be below " + MIN_TEMPERATURE + "°C");
        if (temperature > 60.0) throw new IllegalArgumentException("Temperature cannot be above " + MAX_TEMPERATURE + "°C");
        this.temperature = temperature;
    }

    public double getTemperature() {
        return this.temperature;
    }

    public void setHumidity(double humidity) {
        if (humidity < 0) throw new IllegalArgumentException("Humidity cannot be below zero");
        if (humidity > 100) throw new IllegalArgumentException("Humidity cannot be above 100%");
        this.humidity = humidity;
    }

    public double getHumidity() {
        return this.humidity;
    }

    public void setUvIndex(double uvIndex) {
        if (uvIndex < 0) throw new IllegalArgumentException("UV index cannot be less than zero");
        this.uvIndex = uvIndex;
    }

    public double getUvIndex() {
        return this.uvIndex;
    }

    public void setWindSpeed(double windSpeed) {
        if (windSpeed < 0) throw new IllegalArgumentException("Wind speed less than zero is not allowed");
        this.windSpeed = windSpeed;
    }

    public double getWindSpeed() {
        return this.windSpeed;
    }

    public void setCondition(String condition) {
        if (condition == null) throw new IllegalArgumentException("Condition cannot be null");
        if (condition.isBlank()) throw new IllegalArgumentException("Condition cannot be empty.");
        this.condition = condition;
    }

    public String getCondition() {
        return this.condition;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WeatherData that = (WeatherData) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
