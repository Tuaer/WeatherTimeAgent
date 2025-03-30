package me.jirigebauer;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Locale;

public class TempForecastCLI {

    private static final String WELCOME_MESSAGE =
            "Welcome to TempForecastCLI. Enter your request like 'Determine the temperature in London in 2 hours', " +
                    "'1.5 days' or 'in 30 minutes' (or 'exit' to quit):";
    private static final String PROMPT = "> ";
    private static final String INVALID_FORMAT_MESSAGE =
            "Sorry, I can only handle requests like 'Determine the temperature in London in 2 hours', " +
            "'1.5 days' or 'in 90 minutes'.";
    private static final String INVALID_TIME_MESSAGE =
            "Invalid time format. Use something like 'in 2 hours', 'in 1.5 hours', or 'in 30 minutes'.";
    private static final String EMPTY_INPUT_MESSAGE = "Please enter a request.";
    private static final Pattern REQUEST_PATTERN = Pattern.compile("^Determine the temperature in (.*?) in (.*)$",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern TIME_PATTERN = Pattern.compile("in\\s+(\\d+\\.?\\d*)\\s+(hours?|days?|minutes?)",
            Pattern.CASE_INSENSITIVE);

    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)) {
            System.out.println(WELCOME_MESSAGE);
            while(true){
                System.out.print(PROMPT);
                String input = scanner.nextLine().trim();

                if(input.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }
                if(input.isEmpty()){
                    System.out.println(EMPTY_INPUT_MESSAGE);
                    continue;
                }

                String response = processRequest(input);
                System.out.println(response);
            }
        }
    }

    /**
     * Processes a user request to determine the temperature forecast for a specified location and time.
     *
     * @param input The user's request string, e.g., "Determine the temperature in London in 2 hours"
     * @return A string containing the forecasted temperature (e.g., "It will be 15°C in London in 2 hours.")
     */
    static String processRequest(String input){
        TemperatureGenerator generator = new HashBasedGenerator();
        Matcher matcher = REQUEST_PATTERN.matcher(input);
        if(!matcher.matches()) return INVALID_FORMAT_MESSAGE;

        String location = matcher.group(1).trim();
        String timePhrase = "in  " + matcher.group(2).trim();

        double hours = parseTime(timePhrase);
        if(hours < 0) return INVALID_TIME_MESSAGE;

        int temperature = generator.generateTemperature(location, hours);
        String hourLabel = formatHourLabel(hours);
        return String.format(Locale.US, "It will be %d°C in %s in %.1f %s.", temperature, location, hours,  hourLabel);
    }

    /**
     * Parses a time phrase to extract the number of hours for the temperature forecast.
     *
     * @param timePhrase The time portion of the user input, e.g., "in 2 hours"
     * @return The number of hours as an integer (e.g., 2), or -1 if the time phrase is invalid
     */
    static double parseTime(String timePhrase){
        Matcher matcher = TIME_PATTERN.matcher(timePhrase);
        if(!matcher.find()) return -1;

        try {
            double value = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();

            return switch (unit){
                case "day", "days" -> value*24;
                case "hour", "hours" -> value;
                case "minute", "minutes" -> value/60.0;
                default -> -1;
            };
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Determines the appropriate label for the time unit based on the number of hours.
     *
     * @param hours The number of hours
     * @return "hour" if hour is 1, "hours" otherwise
     */
    private static String formatHourLabel(double hours){
        return (hours >= 0.95 && hours <= 1.05) ? "hour" : "hours";
    }
}
