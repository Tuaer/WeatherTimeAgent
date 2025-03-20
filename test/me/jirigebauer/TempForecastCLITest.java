package me.jirigebauer;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TempForecastCLITest {
    @Test
    public void testProcessRequestValidInput(){
        String input = "Determine the temperature in London in 2 hours";
        String result = TempForecastCLI.processRequest(input);
        assertTrue(result.matches("It will be -?\\d+°C in London in 2 hours\\."));
        input = "Determine the temperature in New York in 12 hours";
        result = TempForecastCLI.processRequest(input);
        assertTrue(result.matches("It will be -?\\d+°C in New York in 12 hours\\."));
    }

    @Test
    public void testProcessRequestInvalidFormat(){
        String input = "What's up in Paris";
        String result = TempForecastCLI.processRequest(input);
        assertEquals("Sorry, I can only handle requests like " +
                "'Determine the temperature in London in 2 hours'.", result);
        input = "Determine the temperature in London in -5 hours";
        result = TempForecastCLI.processRequest(input);
        assertEquals("Invalid time format. Use something like 'in 2 hours'.", result);
        input = "Determine the temperature in London in -5 hours";
        result = TempForecastCLI.processRequest(input);
        assertEquals("Invalid time format. Use something like 'in 2 hours'.", result);
        input = "Determine the temperature in London";
        result = TempForecastCLI.processRequest(input);
        assertEquals("Please specify a time (e.g. 'in 2 hours').", result);
    }

    @Test
    public void testParseTimeValid(){
        int hours = TempForecastCLI.parseTime("in 3 hours");
        assertEquals(3, hours);
    }

    @Test
    public void testParseTimeInvalid(){
        int hours = TempForecastCLI.parseTime("in three hours");
        assertEquals(-1, hours);
    }

    @Test
    public void testGenerateTemperatureBound(){
        for(int i =  0; i < 60; i++) {
            int temp = TempForecastCLI.generateTemperature("Berlin", i);
            assertTrue(temp >= -10 && temp <= 35);
        }
    }
}
