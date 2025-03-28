package me.jirigebauer;

import java.util.Random;
import java.util.Scanner;

public class TempForecastCLI {
    private static final int MIN_TEMPERATURE = -10;
    private static final int MAX_TEMPERATURE = 35;
    private static final int MAX_HOURLY_TEMPERATURE_CHANGE = 5;
    private static final Random RANDOM = new Random();

    private static final String WELCOME_MESSAGE =
            "Welcome to TempForecastCLI. Enter your request (or 'exit'  to quit):";
    private static final String PROMPT = "> ";
    private static final String INVALID_FORMAT_MESSAGE =
            "Sorry, I can only handle requests like 'Determine the temperature in London in 2 hours'.";
    private static final String INVALID_TIME_MESSAGE = "Invalid time format. Use something like 'in 2 hours'.";
    private static final String MISSING_TIME_MESSAGE = "Please specify a time (e.g. 'in 2 hours').";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(WELCOME_MESSAGE);

        while(true){
            System.out.print(PROMPT);
            String input = scanner.nextLine().trim();

            if(input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }
            if(input.isEmpty()){
                System.out.println("Please enter a request.");
                continue;
            }

            String response = processRequest(input);
            System.out.println(response);
        }

        scanner.close();
    }

    /**
     * Processes a user request to determine the temperature forecast for a specified location and time.
     *
     * @param input The user's request string, e.g., "Determine the temperature in London in 2 hours"
     * @return A string containing the forecasted temperature (e.g., "It will be 15°C in London in 2 hours.")
     */
    static String processRequest(String input){
        String[] parts = input.split("\\s+");
        if(parts.length < 5 || !input.startsWith("Determine the temperature in") || !input.contains(" in "))
            return INVALID_FORMAT_MESSAGE;

        int inIndex = input.indexOf(" in ");
        int secondInIndex = input.indexOf(" in ", inIndex+4);
        if(secondInIndex == -1) return MISSING_TIME_MESSAGE;

        String location = input.substring(inIndex+4, secondInIndex).trim();
        String timePhrase = input.substring(secondInIndex).trim();

        int hours = parseTime(timePhrase);
        if(hours < 0) return INVALID_TIME_MESSAGE;

        int temperature = generateTemperature(location, hours);

        String hourLabel = hours == 1 ? "hour" : "hours";
        return "It will be " + temperature + "°C in " + location + " in " + hours + " " + hourLabel + ".";
    }

    /**
     * Parses a time phrase to extract the number of hours for the temperature forecast.
     *
     * @param timePhrase The time portion of the user input, e.g., "in 2 hours"
     * @return The number of hours as an integer (e.g., 2), or -1 if the time phrase is invalid
     */
    static int parseTime(String timePhrase){
        String[] timeParts = timePhrase.split("\\s+");
        if(timeParts.length >= 3 && timeParts[0].equals("in") && timeParts[2].startsWith("hour")) {
            try{
                return Integer.parseInt(timeParts[1]);
            } catch(NumberFormatException e){
                return -1;
            }
        }
        return -1;
    }

    /**
     * Generates a simulated forecast for a given location and a time horizon.
     *
     * @param location  The name of the location for which to generate the temperature (e.g., "London")
     * @param hours     The number of hours into the future fot the forecast (e.g., 2)
     * @return The forecasted temperature in degrees Celsius, between -10 and 35 inclusive
     */
    static int generateTemperature(String location, int hours){
        RANDOM.setSeed(location.hashCode());
        int current = RANDOM.nextInt(MAX_TEMPERATURE - MIN_TEMPERATURE + 1) + MIN_TEMPERATURE;
        for(int i = 0; i < hours; i++) {
            int change = RANDOM.nextInt(2*MAX_HOURLY_TEMPERATURE_CHANGE+1)-MAX_HOURLY_TEMPERATURE_CHANGE;
            current += change;
            if(current > MAX_TEMPERATURE) current = MAX_TEMPERATURE;
            else if(current < MIN_TEMPERATURE) current = MIN_TEMPERATURE;
        }
        return current;
    }
}
