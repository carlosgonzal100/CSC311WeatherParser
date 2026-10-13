package org.example.csc311weatherparser;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/// Builds the text shown to the user, using **text blocks** (Java 15).
///
/// Every message the program prints comes from this interface, so all output
/// is generated in one place. This is an **interface that only holds static
/// methods and constants**, so the output formatting is organized without
/// declaring a class.
public interface WeatherReport {

    /// The main menu, written as a **text block** (Java 15).
    ///
    /// The `\s` escape sequence (also Java 15) keeps the space after
    /// `Choose an option:`, so the user's input does not touch the colon.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// System.out.print(WeatherReport.MENU);
    /// }
    String MENU = """

            ===== Weather Data Analyzer =====
            1. Average temperature for a month
            2. Days above a temperature threshold
            3. Count of rainy days
            4. Weather categories for a month
            0. Exit
            Choose an option:\s""";

    /// The message shown when the user picks an option that is not on the menu.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// System.out.print(WeatherReport.INVALID_OPTION);
    /// // Invalid option. Please choose 0, 1, 2, 3 or 4.
    /// }
    String INVALID_OPTION = """
            Invalid option. Please choose 0, 1, 2, 3 or 4.
            """;

    /// The message shown when the user exits the program.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// System.out.print(WeatherReport.GOODBYE); // Goodbye!
    /// }
    String GOODBYE = """
            Goodbye!
            """;

    /// The question asking the user for a month.
    ///
    /// The `\s` escape sequence (Java 15) keeps the space at the end, and the
    /// closing `"""` on the same line means the text has no final line break,
    /// so the user types on the same line as the question.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// System.out.print(WeatherReport.MONTH_PROMPT);
    /// // Enter a month (1-12), or x to go back:
    /// }
    String MONTH_PROMPT = """
            Enter a month (1-12), or x to go back:\s""";

    /// The message shown when the user enters an invalid month.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// System.out.print(WeatherReport.INVALID_MONTH);
    /// // Invalid month. Please enter a number from 1 to 12, or x to go back.
    /// }
    String INVALID_MONTH = """
            Invalid month. Please enter a number from 1 to 12, or x to go back.
            """;

    /// The question asking the user for a temperature threshold.
    ///
    /// Like [#MONTH_PROMPT], it ends with `\s` and no line break so the user
    /// types on the same line as the question.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// System.out.print(WeatherReport.THRESHOLD_PROMPT);
    /// // Enter a temperature threshold in °C, or x to go back:
    /// }
    String THRESHOLD_PROMPT = """
            Enter a temperature threshold in °C, or x to go back:\s""";

    /// The message shown when the user enters an invalid temperature threshold.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// System.out.print(WeatherReport.INVALID_THRESHOLD);
    /// // Invalid temperature. Please enter a number, or x to go back.
    /// }
    String INVALID_THRESHOLD = """
            Invalid temperature. Please enter a number, or x to go back.
            """;

    /// Formats the message shown when the CSV file cannot be read.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// WeatherReport.fileError("weatherdata.csv");
    /// // "Could not read weatherdata.csv: weatherdata.csv"
    /// }
    ///
    /// @param reason the error message from the failed read
    /// @return the text to display
    static String fileError(String reason) {
        return format("""
                Could not read weatherdata.csv: %s
                """, reason);
    }

    /// Formats the warning shown when a CSV line is skipped because it is invalid.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// WeatherReport.skippedLine(3, "invalid number", "2023-08-01,hot,71,0.7");
    /// // "Warning: skipped line 3 (invalid number): 2023-08-01,hot,71,0.7"
    /// }
    ///
    /// @param lineNumber the line's position in the file, starting at 1
    /// @param reason     a short description of the problem
    /// @param line       the text of the skipped line
    /// @return the text to display
    static String skippedLine(int lineNumber, String reason, String line) {
        return format("""
                Warning: skipped line %d (%s): %s
                """, lineNumber, reason, line);
    }

    /// Formats the result of an average-temperature calculation.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// String text = WeatherReport.averageTemperature(Month.AUGUST, OptionalDouble.of(31.0));
    /// // "Average temperature for August: 31.0 °C"
    /// }
    ///
    /// @param month   the month that was averaged
    /// @param average the average temperature, or empty if the month has no data
    /// @return the text to display
    static String averageTemperature(Month month, OptionalDouble average) {
        if (average.isEmpty()) {
            return noData(month);
        }
        return format("""
                Average temperature for %s: %.1f °C
                """, monthName(month), average.getAsDouble());
    }

    /// Formats the list of days above a temperature threshold, one day per line.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// List<WeatherRecord> days = WeatherAnalyzer.daysAboveThreshold(records, 38.0);
    /// String text = WeatherReport.daysAboveThreshold(38.0, days);
    /// // Days above 38.0 °C: 3
    /// //   2023-07-10   38.5 °C
    /// //   2023-07-26   38.2 °C
    /// //   2023-08-21   39.9 °C
    /// }
    ///
    /// @param threshold the threshold the user entered, in °C
    /// @param days      the days above the threshold
    /// @return the text to display
    static String daysAboveThreshold(double threshold, List<WeatherRecord> days) {
        String rows = days.stream()
                .map(day -> format("  %s  %5.1f °C", day.date(), day.temperature()))
                .collect(Collectors.joining("\n"));
        return format("""
                Days above %.1f °C: %d
                %s
                """, threshold, days.size(), rows);
    }

    /// Formats the number of rainy days.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// String text = WeatherReport.rainyDays(66, 245);
    /// // "Rainy days: 66 out of 245 days"
    /// }
    ///
    /// @param rainyDays the number of rainy days
    /// @param totalDays the total number of days in the data
    /// @return the text to display
    static String rainyDays(long rainyDays, int totalDays) {
        return format("""
                Rainy days: %d out of %d days
                """, rainyDays, totalDays);
    }

    /// Formats each day of a month with its weather category, one day per line.
    ///
    /// The category comes from [WeatherAnalyzer#categorize(WeatherRecord)].
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// List<WeatherRecord> march = WeatherAnalyzer.recordsForMonth(records, Month.MARCH);
    /// String text = WeatherReport.categories(Month.MARCH, march);
    /// // Weather categories for March:
    /// //   2023-03-01    2.1 °C  Cold
    /// //   ...
    /// }
    ///
    /// @param month the month being shown
    /// @param days  the records for that month
    /// @return the text to display
    static String categories(Month month, List<WeatherRecord> days) {
        if (days.isEmpty()) {
            return noData(month);
        }
        String rows = days.stream()
                .map(day -> format("  %s  %5.1f °C  %s",
                        day.date(), day.temperature(), WeatherAnalyzer.categorize(day)))
                .collect(Collectors.joining("\n"));
        return format("""
                Weather categories for %s:
                %s
                """, monthName(month), rows);
    }

    /// Formats the message shown when a month has no data.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// WeatherReport.noData(Month.JANUARY); // "No data found for January."
    /// }
    ///
    /// @param month the month without data
    /// @return the text to display
    private static String noData(Month month) {
        return format("""
                No data found for %s.
                """, monthName(month));
    }

    /// Returns the full English name of a month, such as `"August"`.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// WeatherReport.monthName(Month.AUGUST); // "August"
    /// }
    ///
    /// @param month the month to name
    /// @return the month's full English name
    private static String monthName(Month month) {
        return month.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    /// Fills a template with values using English number formatting.
    ///
    /// The fixed locale makes decimals always print with a dot (`31.4`), even
    /// on a computer whose language setting would use a comma (`31,4`).
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// WeatherReport.format("%.1f °C", 31.44); // "31.4 °C"
    /// }
    ///
    /// @param template the text with `%` placeholders
    /// @param values   the values to insert
    /// @return the filled-in text
    private static String format(String template, Object... values) {
        return String.format(Locale.ENGLISH, template, values);
    }
}
