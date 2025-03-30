package me.jirigebauer;

import java.util.Random;

public class HashBasedGenerator implements TemperatureGenerator {
    private static final int MIN_TEMPERATURE = -10;
    private static final int MAX_TEMPERATURE = 35;
    private static final int MAX_HOURLY_TEMPERATURE_CHANGE = 5;
    private static final Random RANDOM = new Random();

    /**
     * Generates a simulated forecast for a given location and a time horizon.
     *
     * @param location  The name of the location for which to generate the temperature (e.g., "London")
     * @param hours     The number of hours into the future fot the forecast (e.g., 2)
     * @return The forecasted temperature in degrees Celsius, between -10 and 35 inclusive
     */
    public int generateTemperature(String location, double hours){
        if(hours < 0) throw new IllegalArgumentException("Hours cannot be negative " + hours);
        if(location == null || location.trim().isEmpty())
            throw new IllegalArgumentException("Location cannot be null or empty");

        long seed = location.hashCode();
        RANDOM.setSeed(seed);

        int currentTemp = generateInitialTemperature();
        if(hours == 0) return currentTemp;

        return simulateTemperatureOverTime(currentTemp, hours);
    }

    /**
     * Generates an initial temperature based on the seeded random generator.
     *
     * @return A temperature between MIN_TEMPERATURE and MAX_TEMPERATURE
     */
    private int generateInitialTemperature(){
        return RANDOM.nextInt(MAX_TEMPERATURE - MIN_TEMPERATURE + 1) + MIN_TEMPERATURE;
    }

    /**
     * Simulates temperature changes over the given time period.
     *
     * @param initialTemp   The starting temperature
     * @param hours         The time horizon in hours
     * @return The final temperature after simulation
     */
    private int simulateTemperatureOverTime(int initialTemp, double hours){
        int currentTemp = initialTemp;
        int fullHours = (int) hours;
        double fractionalHours = hours-fullHours;

        for(int i = 0; i < fullHours; i++) {
            currentTemp = applyTemperatureChange(currentTemp);
        }

        if(fractionalHours > 0){
            int change = generateTemperatureChange();
            currentTemp += (int) (change*fractionalHours);
        }

        return clampTemperature(currentTemp);
    }

    /**
     * Generates a random temperature change within the allowed range.
     *
     * @return A change value between -MAX_HOURLY_TEMPERATURE_CHANGE and +MAX_HOURLY_TEMPERATURE_CHANGE
     */
    private int generateTemperatureChange(){
        return RANDOM.nextInt(2*MAX_HOURLY_TEMPERATURE_CHANGE+1)-MAX_HOURLY_TEMPERATURE_CHANGE;
    }

    /**
     * Applies a temperature change and ensures it stays within bounds.
     *
     * @param currentTemp The current temperature
     * @return The new temperature after applying a random change
     */
    private int applyTemperatureChange(int currentTemp){
        int change = generateTemperatureChange();
        return clampTemperature(currentTemp+change);
    }

    /**
     * Clamps the temperature to the valid range.
     *
     * @param temp The temperature to clamp
     * @return The clamped temperature between MIN_TEMPERATURE and MAX_TEMPERATURE
     */
    private int clampTemperature(int temp){
        return Math.min(MAX_TEMPERATURE, Math.max(MIN_TEMPERATURE, temp));
    }
}
