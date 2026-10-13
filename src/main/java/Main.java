import org.example.csc311weatherparser.WeatherAnalyzer;
import org.example.csc311weatherparser.WeatherParser;
import org.example.csc311weatherparser.WeatherRecord;
import org.example.csc311weatherparser.WeatherReport;

/// Starts the Weather Data Analyzer and runs the interactive console menu.
///
/// This file has **no class declaration**. It is a compact source file whose
/// class is declared implicitly (previewed in Java 21–23, final in Java 25), and
/// `main` is an instance method that needs neither `public`, `static` nor a
/// `String[]` parameter. A compact source file also imports the whole
/// `java.base` module automatically, so `List`, `Path`, `Scanner`, `Month` and
/// the other JDK types used here need no import statements.
///
/// The menu choice is handled by an **enhanced switch expression** that
/// returns whether the menu should keep running.
///
/// ## Example
/// Run from the project folder, which contains `weatherdata.csv`:
/// {@snippet lang=java :
/// // Choose an option: 3
/// // Rainy days: 66 out of 245 days
/// }
void main() {
    List<WeatherRecord> records;
    try {
        records = WeatherParser.parse(Path.of("weatherdata.csv"));
    } catch (IOException e) {
        System.out.print(WeatherReport.fileError(e.getMessage()));
        return;
    }

    Scanner scanner = new Scanner(System.in);
    boolean running = true;
    while (running) {
        System.out.print(WeatherReport.MENU);
        if (!scanner.hasNextLine()) {
            break;
        }
        String choice = scanner.nextLine().trim();
        running = switch (choice) {
            case "1" -> {
                readMonth(scanner).ifPresent(month -> System.out.print(WeatherReport.averageTemperature(
                        month, WeatherAnalyzer.averageTemperature(records, month))));
                yield true;
            }
            case "2" -> {
                readThreshold(scanner).ifPresent(threshold -> System.out.print(WeatherReport.daysAboveThreshold(
                        threshold, WeatherAnalyzer.daysAboveThreshold(records, threshold))));
                yield true;
            }
            case "3" -> {
                System.out.print(WeatherReport.rainyDays(
                        WeatherAnalyzer.countRainyDays(records), records.size()));
                yield true;
            }
            case "4" -> {
                readMonth(scanner).ifPresent(month -> System.out.print(WeatherReport.categories(
                        month, WeatherAnalyzer.recordsForMonth(records, month))));
                yield true;
            }
            case "0" -> false;
            default -> {
                System.out.print(WeatherReport.INVALID_OPTION);
                yield true;
            }
        };
    }
    System.out.print(WeatherReport.GOODBYE);
}

/// Asks the user for a month number until a valid one is entered.
///
/// - A number from `1` to `12` is accepted and returned as a `Month`.
/// - `x` (or `X`) cancels the question and returns an empty `Optional`, so
///   the caller goes back to the menu.
/// - Anything else prints an error message and asks again.
///
/// ## Example
/// {@snippet lang=java :
/// // Enter a month (1-12), or x to go back: 13
/// // Invalid month. Please enter a number from 1 to 12, or x to go back.
/// // Enter a month (1-12), or x to go back: 8
/// Optional<Month> month = readMonth(scanner); // Optional[AUGUST]
/// }
///
/// @param scanner the scanner reading the user's input
/// @return the month the user entered, or an empty `Optional` if the user typed `x`
Optional<Month> readMonth(Scanner scanner) {
    while (true) {
        System.out.print(WeatherReport.MONTH_PROMPT);
        if (!scanner.hasNextLine()) {
            return Optional.empty();
        }
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("x")) {
            return Optional.empty();
        }
        try {
            return Optional.of(Month.of(Integer.parseInt(input)));
        } catch (NumberFormatException | DateTimeException e) {
            System.out.print(WeatherReport.INVALID_MONTH);
        }
    }
}

/// Asks the user for a temperature threshold in °C until a valid one is entered.
///
/// - Any number, such as `35` or `28.5`, is accepted.
/// - `x` (or `X`) cancels the question and returns an empty `OptionalDouble`,
///   so the caller goes back to the menu.
/// - Anything else prints an error message and asks again.
///
/// ## Example
/// {@snippet lang=java :
/// // Enter a temperature threshold in °C, or x to go back: abc
/// // Invalid temperature. Please enter a number, or x to go back.
/// // Enter a temperature threshold in °C, or x to go back: 35
/// OptionalDouble threshold = readThreshold(scanner); // OptionalDouble[35.0]
/// }
///
/// @param scanner the scanner reading the user's input
/// @return the threshold the user entered, or an empty `OptionalDouble` if the user typed `x`
OptionalDouble readThreshold(Scanner scanner) {
    while (true) {
        System.out.print(WeatherReport.THRESHOLD_PROMPT);
        if (!scanner.hasNextLine()) {
            return OptionalDouble.empty();
        }
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("x")) {
            return OptionalDouble.empty();
        }
        try {
            double threshold = Double.parseDouble(input);
            if (Double.isFinite(threshold)) {
                return OptionalDouble.of(threshold);
            }
        } catch (NumberFormatException e) {
            // Not a number: fall through to the error message and ask again.
        }
        System.out.print(WeatherReport.INVALID_THRESHOLD);
    }
}
