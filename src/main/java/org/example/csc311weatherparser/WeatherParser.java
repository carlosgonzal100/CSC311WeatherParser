package org.example.csc311weatherparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/// Reads weather data from a CSV file into [WeatherRecord]s.
///
/// This is an **interface that only holds static methods**, so parsing is
/// organized without declaring a class.
public interface WeatherParser {

    /// Reads every data line of a CSV file into a list of [WeatherRecord]s.
    ///
    /// The file is processed as a **stream** of lines:
    /// 1. the header line is skipped,
    /// 2. blank lines are removed with a **lambda** filter,
    /// 3. each remaining line is converted with the method reference
    ///    `WeatherRecord::fromCsvLine`,
    /// 4. the results are collected with `Stream.toList()` (Java 16), which
    ///    returns an unmodifiable list.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// List<WeatherRecord> records = WeatherParser.parse(Path.of("weatherdata.csv"));
    /// records.size(); // 245
    /// }
    ///
    /// @param path the location of the CSV file
    /// @return all records in the file, in the same order as the file
    /// @throws IOException if the file cannot be read
    static List<WeatherRecord> parse(Path path) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .map(WeatherRecord::fromCsvLine)
                    .toList();
        }
    }
}
