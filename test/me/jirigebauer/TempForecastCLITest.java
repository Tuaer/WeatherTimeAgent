package me.jirigebauer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TempForecastCLITest {
    private static final String INVALID_REQUEST_MESSAGE =
            "Sorry, I can only handle requests like 'Determine the temperature in London in 2 hours', " +
                    "'1.5 days' or 'in 90 minutes'.";
    private static final String INVALID_TIME_MESSAGE =
            "Invalid time format. Use something like 'in 2 hours', 'in 1.5 hours', or 'in 30 minutes'.";

    @Test
    public void testProcessRequestValidInput(){
        String input = "Determine the temperature in London in 2 hours";
        String result = TempForecastCLI.processRequest(input);
        assertTrue(result.matches("It will be -?\\d+°C in London in 2\\.?0* hours\\."));

        input = "Determine the temperature in New York in 12 hours";
        result = TempForecastCLI.processRequest(input);
        assertTrue(result.matches("It will be -?\\d+°C in New York in 12\\.?0* hours\\."));
    }

    @Test
    public void testProcessRequestInvalidFormat(){
        String input = "What's up in Paris";
        String result = TempForecastCLI.processRequest(input);
        assertEquals(INVALID_REQUEST_MESSAGE, result);

        input = "Determine the temperature in London in -5 hours";
        result = TempForecastCLI.processRequest(input);
        assertEquals(INVALID_TIME_MESSAGE, result);
    }

    @Test
    public void testParseTimeValid(){
        double hours = TempForecastCLI.parseTime("in 3 hours");
        assertEquals(3.0, hours, 0.001);

        hours = TempForecastCLI.parseTime("in 2.5 hours");
        assertEquals(2.5, hours, 0.001);

        hours = TempForecastCLI.parseTime("in 1 day");
        assertEquals(24.0, hours, 0.001);

        hours = TempForecastCLI.parseTime("in 30 minutes");
        assertEquals(0.5, hours, 0.001);
    }

    @Test
    public void testParseTimeInvalid(){
        double hours = TempForecastCLI.parseTime("in three hours");
        assertEquals(-1, hours, 0.001);

        hours = TempForecastCLI.parseTime("in -2 hours");
        assertEquals(-1, hours, 0.001);
    }

    @Test
    public void testGenerateTemperatureBound(){
        TemperatureGenerator generator = new HashBasedGenerator();
        for(int i =  0; i < 60; i++) {
            int temp = generator.generateTemperature("Berlin", i);
            assertTrue(temp >= -10 && temp <= 35, "Temperature out of bounds: " + temp);
        }
    }

    @Test
    public void testProcessRequestWithFractionalHours(){
        String input = "Determine the temperature in Paris in 2.5 hours";
        String result = TempForecastCLI.processRequest(input);
        assertTrue(result.matches("It will be -?\\d+°C in Paris in 2\\.5 hours\\."));
    }

    @Test
    public void testProcessRequestWithDaysAndMinutes(){
        String input = "Determine the temperature in Berlin in 2 days";
        String result = TempForecastCLI.processRequest(input);
        assertTrue(result.matches("It will be -?\\d+°C in Berlin in 48\\.0 hours\\."));

        input = "Determine the temperature in Tokyo in 90 minutes";
        result = TempForecastCLI.processRequest(input);
        assertTrue(result.matches("It will be -?\\d+°C in Tokyo in 1\\.5 hours\\."));
    }

    @Test
    public void testProcessRequestHourLabelSingular(){
        String input = "Determine the temperature in London in 1 hour";
        String result = TempForecastCLI.processRequest(input);
        assertTrue(result.matches("It will be -?\\d+°C in London in 1\\.0 hour\\."));
    }

    @Test
    public void testGenerateTemperatureWithZeroHours(){
        TemperatureGenerator generator = new HashBasedGenerator();
        int temp = generator.generateTemperature("London", 0.0);
        assertTrue(temp >= -10 && temp <= 35);
    }

    @Test
    public void testGenerateTemperatureThrowsOnNegativeHours(){
        TemperatureGenerator generator = new HashBasedGenerator();
        assertThrows(IllegalArgumentException.class, () -> generator.generateTemperature("London", -1.0));
    }

    @Test
    public void testGenerateTemperatureThrowsOnInvalidLocation(){
        TemperatureGenerator generator = new HashBasedGenerator();
        assertThrows(IllegalArgumentException.class, () -> generator.generateTemperature(null, 2.0));
        assertThrows(IllegalArgumentException.class, () -> generator.generateTemperature("", 2.0));
    }

    @Test
    public void testGenerateTemperatureVariesWithHours(){
        TemperatureGenerator generator = new HashBasedGenerator();
        int temp1 = generator.generateTemperature("London", 2.0);
        int temp2 = generator.generateTemperature("London", 3.0);
        assertNotEquals(temp1, temp2, "Temperatures should vary with different hours");
    }
}
