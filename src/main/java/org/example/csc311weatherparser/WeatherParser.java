package org.example.csc311weatherparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/// Reads weather data from a CSV file into [WeatherRecord]s.
///
/// This is an **interface that only holds static methods**, so parsing is
/// organized without declaring a class.
public interface WeatherParser {

    /// Reads every data line of a CSV file into a list of [WeatherRecord]s.
    ///
    /// The lines are processed as a **stream** of line indexes, so each line's
    /// number is known for warnings:
    /// 1. the header line (line 1) is skipped,
    /// 2. blank lines are removed with a **lambda** filter,
    /// 3. each remaining line is converted with [#parseLine(String, int)],
    ///    which returns an empty `Optional` for a bad line,
    /// 4. `flatMap(Optional::stream)` keeps only the lines that were parsed,
    /// 5. the results are collected with `Stream.toList()` (Java 16), which
    ///    returns an unmodifiable list.
    ///
    /// A bad line does not stop the program: a warning is printed and the line
    /// is skipped.
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// List<WeatherRecord> records = WeatherParser.parse(Path.of("weatherdata.csv"));
    /// records.size(); // 245
    ///
    /// // If line 200 contained "2023-13-01,29.5,71,0.7", this would be printed
    /// // and the other lines would still be read:
    /// // Warning: skipped line 200 (invalid date): 2023-13-01,29.5,71,0.7
    /// }
    ///
    /// @param path the location of the CSV file
    /// @return all valid records in the file, in the same order as the file
    /// @throws IOException if the file cannot be read
    static List<WeatherRecord> parse(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        return IntStream.range(1, lines.size())
                .filter(index -> !lines.get(index).isBlank())
                .mapToObj(index -> parseLine(lines.get(index), index + 1))
                .flatMap(Optional::stream)
                .toList();
    }

    /// Converts one CSV line into a [WeatherRecord], or prints a warning if the
    /// line is invalid.
    ///
    /// The reason in the warning is chosen with **pattern matching for switch**
    /// (Java 21) on the type of the exception, using **unnamed variables** `_`
    /// (Java 22) because the exception itself is not needed.
    ///
    /// | Problem in the line          | Reason shown       |
    /// |------------------------------|--------------------|
    /// | fewer than four values       | `missing values`   |
    /// | date not in `yyyy-MM-dd`     | `invalid date`     |
    /// | number that cannot be parsed | `invalid number`   |
    ///
    /// ## Example
    /// {@snippet lang=java :
    /// parseLine("2023-08-01,29.5,71,0.7", 2); // Optional[WeatherRecord[...]]
    ///
    /// parseLine("2023-08-01,hot,71,0.7", 3);  // Optional.empty
    /// // prints: Warning: skipped line 3 (invalid number): 2023-08-01,hot,71,0.7
    /// }
    ///
    /// @param line       the text of the line
    /// @param lineNumber the line's position in the file, starting at 1, used in the warning
    /// @return the parsed record, or an empty `Optional` if the line is invalid
    private static Optional<WeatherRecord> parseLine(String line, int lineNumber) {
        try {
            return Optional.of(WeatherRecord.fromCsvLine(line));
        } catch (ArrayIndexOutOfBoundsException | DateTimeException | NumberFormatException e) {
            String reason = switch (e) {
                case ArrayIndexOutOfBoundsException _ -> "missing values";
                case DateTimeException _ -> "invalid date";
                default -> "invalid number";
            };
            System.out.print(WeatherReport.skippedLine(lineNumber, reason, line));
            return Optional.empty();
        }
    }
}
