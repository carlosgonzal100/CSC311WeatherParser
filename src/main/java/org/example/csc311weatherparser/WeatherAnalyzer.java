package org.example.csc311weatherparser;

import java.time.Month;
import java.util.List;
import java.util.OptionalDouble;

/// Analyses a list of [WeatherRecord]s using **lambdas** and **streams**.
///
/// This is an **interface that only holds static methods**, so the analysis
/// logic is organized without declaring a class.
public interface WeatherAnalyzer {

    /// Returns the records whose date falls in the given month.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// List<WeatherRecord> august = WeatherAnalyzer.recordsForMonth(records, Month.AUGUST);
    /// august.size(); // 31
    /// }
    ///
    /// @param records the weather data to search
    /// @param month   the month to keep
    /// @return the matching records, in their original order
    static List<WeatherRecord> recordsForMonth(List<WeatherRecord> records, Month month) {
        return records.stream()
                .filter(day -> day.date().getMonth() == month)
                .toList();
    }

    /// Calculates the average temperature for a specific month.
    ///
    /// The result is an `OptionalDouble` because the month might have no data
    /// (for example January, since `weatherdata.csv` covers March to October).
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// OptionalDouble average = WeatherAnalyzer.averageTemperature(records, Month.AUGUST);
    /// average.isPresent();  // true
    /// average.getAsDouble(); // the mean of all August temperatures
    ///
    /// WeatherAnalyzer.averageTemperature(records, Month.JANUARY).isEmpty(); // true
    /// }
    ///
    /// @param records the weather data to analyse
    /// @param month   the month to average
    /// @return the average temperature in °C, or an empty `OptionalDouble` if the month has no data
    static OptionalDouble averageTemperature(List<WeatherRecord> records, Month month) {
        return recordsForMonth(records, month).stream()
                .mapToDouble(WeatherRecord::temperature)
                .average();
    }

    /// Returns the days whose temperature is above the given threshold.
    ///
    /// A day exactly equal to the threshold is **not** included.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// List<WeatherRecord> hotDays = WeatherAnalyzer.daysAboveThreshold(records, 35.0);
    /// hotDays.forEach(day -> System.out.println(day.date()));
    /// }
    ///
    /// @param records   the weather data to search
    /// @param threshold the temperature in °C that a day must be above
    /// @return the matching records, in their original order
    static List<WeatherRecord> daysAboveThreshold(List<WeatherRecord> records, double threshold) {
        return records.stream()
                .filter(day -> day.temperature() > threshold)
                .toList();
    }

    /// Counts the rainy days, meaning days with precipitation greater than `0.0`.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// long rainyDays = WeatherAnalyzer.countRainyDays(records); // 66
    /// }
    ///
    /// @param records the weather data to analyse
    /// @return the number of days with precipitation greater than `0.0`
    static long countRainyDays(List<WeatherRecord> records) {
        return records.stream()
                .filter(day -> day.precipitation() > 0.0)
                .count();
    }

    /// Determines the weather category of a day from its temperature.
    ///
    /// | Category | Temperature        |
    /// |----------|--------------------|
    /// | `Hot`    | 30 °C and above    |
    /// | `Warm`   | 20 °C to 29.9 °C   |
    /// | `Cold`   | below 20 °C        |
    ///
    /// This method uses several modern `switch` features:
    /// - an **enhanced switch expression** with `->` arrows that returns a value (Java 14),
    /// - **pattern matching for switch** with `when` guards (Java 21),
    /// - a **record pattern** that takes the [WeatherRecord] apart (Java 21),
    /// - **unnamed variables** `_` for the components that are not needed (Java 22).
    ///
    /// The last case matches every remaining record, so the switch is
    /// exhaustive without a `default` branch.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// WeatherRecord day = WeatherRecord.fromCsvLine("2023-08-21,39.9,25,0.0");
    /// WeatherAnalyzer.categorize(day); // "Hot"
    /// }
    ///
    /// @param day the record to categorize
    /// @return `"Hot"`, `"Warm"` or `"Cold"`
    static String categorize(WeatherRecord day) {
        return switch (day) {
            case WeatherRecord(_, double temperature, _, _) when temperature >= 30.0 -> "Hot";
            case WeatherRecord(_, double temperature, _, _) when temperature >= 20.0 -> "Warm";
            case WeatherRecord(_, _, _, _) -> "Cold";
        };
    }
}
