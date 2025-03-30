package me.jirigebauer;

/**
 * Defines a contract for generating temperature forecasts based on location and time.
 */
public interface TemperatureGenerator {
    /**
     * Generates a temperature forecast for a given location and time in the future.
     *
     * @param location  The name of the location (e.g., "London")
     * @param hours     The time in hours from now for the forecast
     * @return The forecasted temperature in degrees Celsius
     */
    int generateTemperature(String location, double hours);

    static TemperatureGenerator defaultGenerator(){
        return (location, hours) -> 0;
    }
}
