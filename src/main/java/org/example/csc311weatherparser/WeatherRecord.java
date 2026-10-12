package org.example.csc311weatherparser;

import java.time.LocalDate;

/// One day of weather data, read from a single line of `weatherdata.csv`.
///
/// A **record** (Java 16) is an immutable data carrier. The compiler generates
/// the constructor, the accessor methods ([#date()], [#temperature()],
/// [#humidity()] and [#precipitation()]), `equals`, `hashCode` and `toString`.
///
/// ## Example
/// {@snippet lang=java :
/// WeatherRecord day = new WeatherRecord(LocalDate.of(2023, 8, 1), 29.5, 71, 0.7);
/// double temperature = day.temperature(); // 29.5
/// }
///
/// @param date          the day the measurements were taken
/// @param temperature   the temperature in degrees Celsius
/// @param humidity      the relative humidity as a whole-number percentage
/// @param precipitation the precipitation for the day; `0.0` means no rain
public record WeatherRecord(LocalDate date, double temperature, int humidity, double precipitation) {

    /// Creates a [WeatherRecord] from one data line of the CSV file.
    ///
    /// The line must contain four comma-separated values in the order
    /// `Date,Temperature,Humidity,Precipitation`, with the date written as
    /// `yyyy-MM-dd`.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// WeatherRecord day = WeatherRecord.fromCsvLine("2023-08-01,29.5,71,0.7");
    /// day.date();          // 2023-08-01
    /// day.humidity();      // 71
    /// day.precipitation(); // 0.7
    /// }
    ///
    /// @param line one data line from the CSV file (not the header line)
    /// @return the record holding the values from `line`
    /// @throws ArrayIndexOutOfBoundsException if the line has fewer than four values
    /// @throws java.time.format.DateTimeParseException if the date is not in `yyyy-MM-dd` format
    /// @throws NumberFormatException if a numeric value cannot be parsed
    public static WeatherRecord fromCsvLine(String line) {
        String[] values = line.split(",");
        return new WeatherRecord(
                LocalDate.parse(values[0].trim()),
                Double.parseDouble(values[1].trim()),
                Integer.parseInt(values[2].trim()),
                Double.parseDouble(values[3].trim()));
    }
}
