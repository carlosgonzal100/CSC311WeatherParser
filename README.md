# Weather Data Analyzer

A console application that reads daily weather data from a CSV file and analyzes it using **modern Java features (Java 15–23 and later)**, built **without a single explicit class**. The whole design uses only records, interfaces, lambdas and streams.

> **Course:** CSC 311 – Advanced Programming<br>
> **Author:** [Carlos Gonzalez](https://github.com/carlosgonzal100) with the assistance of CLAUDE.AI

---

## Table of Contents
1. [Features](#features)
2. [Requirements](#requirements)
3. [How to Run](#how-to-run)
4. [Sample Session](#sample-session)
5. [CSV Data Format](#csv-data-format)
6. [Modern Java Features Used](#modern-java-features-used)
7. [Design: No Explicit Classes](#design-no-explicit-classes)
8. [Project Structure](#project-structure)
9. [Documentation (Markdown Javadoc)](#documentation-markdown-javadoc)
10. [Error Handling](#error-handling)
11. [AI Disclosure Note](#ai-disclosure-note)

---

## Features

| # | Menu option | What it does |
|---|-------------|--------------|
| 1 | **Average temperature for a month** | Averages the temperature of every day in the chosen month (1–12). |
| 2 | **Days above a temperature threshold** | Lists every day whose temperature is above the number you enter. |
| 3 | **Count of rainy days** | Counts the days with precipitation greater than `0.0`. |
| 4 | **Weather categories for a month** | Shows every day of a month labelled **Hot**, **Warm** or **Cold** using an enhanced `switch`. |
| 0 | **Exit** | Closes the program. |

**Weather categories**

| Category | Temperature |
|----------|-------------|
| Hot  | 30 °C and above |
| Warm | 20 °C to 29.9 °C |
| Cold | below 20 °C |

---

## Requirements

- **JDK 25** (or newer)
- No other installation is needed. The project includes the **Maven Wrapper** (`mvnw` / `mvnw.cmd`), which downloads Maven automatically.

> ### ⚠️ Why JDK 25 and not JDK 23?
> `Main.java` is written **without a class declaration** (an *implicitly declared class* with an instance `void main()`), so the project has **no explicit classes**.
> This feature was a *preview* in Java 21, 22 and 23 and became a **final, standard feature in Java 25** (JEP 512).
> On JDK 23 it only compiles with `--enable-preview`, and preview features compiled for one JDK version cannot be built on another. Targeting JDK 25 is the only way to use it without preview flags.
> **Every other feature in the project comes from Java 15–23.**

---

## How to Run

The program must be started from the **project root folder** (the folder containing `weatherdata.csv`).

### Option 1 – Maven (command line)

**Windows (PowerShell or Command Prompt):**
```bash
.\mvnw.cmd clean javafx:run
```

**macOS / Linux:**
```bash
./mvnw clean javafx:run
```

If Maven is installed globally, `mvn clean javafx:run` also works.

### Option 2 – IntelliJ IDEA
1. Open the project folder in IntelliJ (it is detected as a Maven project).
2. Make sure the project SDK is **JDK 25** (*File → Project Structure → Project → SDK*).
3. Right-click `src/main/java/Main.java` → **Run 'Main'**.

---

## Sample Session

```
===== Weather Data Analyzer =====
1. Average temperature for a month
2. Days above a temperature threshold
3. Count of rainy days
4. Weather categories for a month
0. Exit
Choose an option: 1
Enter a month (1-12), or x to go back: abc
Invalid month. Please enter a number from 1 to 12, or x to go back.
Enter a month (1-12), or x to go back: 8
Average temperature for August: 31.0 °C

Choose an option: 2
Enter a temperature threshold in °C, or x to go back: 38
Days above 38.0 °C: 3
  2023-07-10   38.5 °C
  2023-07-26   38.2 °C
  2023-08-21   39.9 °C

Choose an option: 3
Rainy days: 66 out of 245 days

Choose an option: 4
Enter a month (1-12), or x to go back: 8
Weather categories for August:
  2023-08-01   29.5 °C  Warm
  2023-08-02   27.7 °C  Warm
  2023-08-03   33.9 °C  Hot
  ...

Choose an option: 0
Goodbye!
```

- When an input is invalid, the program **asks again** until it gets a valid value.
- Typing **`x`** at any month or threshold question goes **back to the menu**.

---

## CSV Data Format

The program reads `weatherdata.csv` from the project root. The first line is a header, and every following line is one day:

```csv
Date,Temperature,Humidity,Precipitation
2023-08-01,29.5,71,0.7
2023-08-02,27.7,50,0.0
```

| Column | Java type | Description |
|--------|-----------|-------------|
| `Date` | `LocalDate` | Date in `yyyy-MM-dd` format |
| `Temperature` | `double` | Temperature in degrees Celsius |
| `Humidity` | `int` | Relative humidity, whole-number percentage |
| `Precipitation` | `double` | Rainfall for the day; `0.0` means no rain |

The included file has **245 days** (2023-03-01 to 2023-10-31). The data is **synthetic** (generated for this project, not real weather records). It was designed to include cold, warm and hot days and 66 rainy days.

**Using your own file:** replace the contents of `weatherdata.csv` (or put a different file with that exact name in the project root) with data that has the same four columns **in the same order**, the same date format, and a header line.

---

## Modern Java Features Used

| Feature | Java version | Where it is used |
|---------|--------------|------------------|
| **Records** | 16 | `WeatherRecord`: one immutable day of weather data |
| **Enhanced `switch` expression** (`->`, `yield`) | 14 | Menu handling in `Main.main()`; categories in `WeatherAnalyzer.categorize()` |
| **Text blocks** (`"""`) | 15 | All program output in `WeatherReport` (menu, results, prompts, errors, warnings) |
| **`\s` escape in text blocks** | 15 | Keeps the trailing space in the menu and input prompts |
| **`Stream.toList()`** | 16 | Collecting results in `WeatherParser` and `WeatherAnalyzer` |
| **Pattern matching for `switch`** with `when` guards | 21 | `WeatherAnalyzer.categorize()` (Hot/Warm/Cold); `WeatherParser.parseLine()` (choosing the warning reason by exception type) |
| **Record patterns** | 21 | `case WeatherRecord(_, double temperature, _, _)` in `categorize()` |
| **Unnamed variables `_`** | 22 | Ignored record components and exception variables in `switch` cases |
| **Markdown documentation comments (`///`)** | 23 | Every type, method and constant |
| **`{@snippet}` code examples in Javadoc** | 18 | A code example in every doc comment |
| **Lambdas and method references** | 8+ | Stream filters (`day -> day.temperature() > threshold`), `WeatherRecord::fromCsvLine`, `Optional::stream`, `ifPresent(...)` |
| **Streams** (`Stream`, `IntStream`, `DoubleStream`) | 8+ | Parsing, filtering, averaging, counting and joining output |
| **Interface static and private methods** | 8 / 9 | Every interface in the project |
| **Implicitly declared class + instance `main`** | 25 (preview 21–23) | `Main.java` |

### The Hot / Warm / Cold switch
```java
static String categorize(WeatherRecord day) {
    return switch (day) {
        case WeatherRecord(_, double temperature, _, _) when temperature >= 30.0 -> "Hot";
        case WeatherRecord(_, double temperature, _, _) when temperature >= 20.0 -> "Warm";
        case WeatherRecord(_, _, _, _) -> "Cold";
    };
}
```
This single `switch` combines an **enhanced switch expression**, **pattern matching**, a **record pattern**, **`when` guards** and **unnamed variables**. The last case matches every remaining record, so the switch is exhaustive without a `default` branch.

### Parsing with streams and lambdas
```java
List<String> lines = Files.readAllLines(path);
return IntStream.range(1, lines.size())                         // skip the header line
        .filter(index -> !lines.get(index).isBlank())           // ignore blank lines
        .mapToObj(index -> parseLine(lines.get(index), index + 1))
        .flatMap(Optional::stream)                              // drop invalid lines
        .toList();
```

---

## Design: No Explicit Classes

The project contains **no `class` declarations** and no anonymous classes. Every part of the program is one of the following:

| Construct | Used for |
|-----------|----------|
| **record** | Data: `WeatherRecord` |
| **interface with static methods** | Behavior grouped by responsibility: `WeatherParser`, `WeatherAnalyzer`, `WeatherReport`. These interfaces are never implemented. They work like namespaces of functions, similar to calling `Math.max(...)`. |
| **lambdas / method references** | All processing logic |
| **implicitly declared class** | `Main.java` contains only `void main()` and two helper methods, with no class declaration |

Each file has **one responsibility**:

```
WeatherParser   → reads the CSV file          (input)
WeatherRecord   → holds one day of data       (data)
WeatherAnalyzer → calculates the results      (logic)
WeatherReport   → builds all text output      (output)
Main            → runs the menu, connects the pieces
```

---

## Project Structure

```
CSC311WeatherParser/
├── README.md
├── weatherdata.csv                     ← input data (245 days)
├── pom.xml                             ← Maven build (Java 25)
├── mvnw / mvnw.cmd                     ← Maven Wrapper
└── src/main/java/
    ├── Main.java                       ← entry point, menu (no class declaration)
    └── org/example/csc311weatherparser/
        ├── WeatherRecord.java          ← record: one day of weather data
        ├── WeatherParser.java          ← interface: CSV parsing
        ├── WeatherAnalyzer.java        ← interface: calculations + Hot/Warm/Cold switch
        └── WeatherReport.java          ← interface: all output as text blocks
```

---

## Documentation (Markdown Javadoc)

Every type, method and constant is documented with **Markdown documentation comments**: lines starting with `///` instead of the classic `/** ... */`. Each comment includes a **code example** in a `{@snippet}` block.

```java
/// Counts the rainy days, meaning days with precipitation greater than `0.0`.
///
/// ## Example
/// {@snippet lang=java :
/// long rainyDays = WeatherAnalyzer.countRainyDays(records); // 66
/// }
///
/// @param records the weather data to analyse
/// @return the number of days with precipitation greater than `0.0`
static long countRainyDays(List<WeatherRecord> records) { ... }
```

> **Note on versions:** The assignment refers to "markdown support introduced in JDK 18." Two separate features cover this, and the project uses **both**:
> - **JDK 18 – JEP 413:** the `{@snippet}` tag for code examples in Javadoc.
> - **JDK 23 – JEP 467:** Markdown documentation comments (`///`).

### Generating the HTML documentation
From the project root (requires the `javadoc` tool from JDK 23 or newer):
```bash
javadoc -d docs -sourcepath src/main/java -subpackages org.example
```
Then open `docs/index.html` in a browser.

---

## Error Handling

| Situation | What the program does |
|-----------|-----------------------|
| Invalid month (e.g. `13`, `abc`) | Shows an error and asks again |
| Invalid threshold (e.g. `hot`, `NaN`) | Shows an error and asks again |
| `x` typed at a month or threshold question | Returns to the menu |
| Invalid menu option | Shows an error and displays the menu again |
| Month with no data (e.g. January) | `No data found for January.` |
| Invalid line in the CSV file | Skips the line and prints a warning, e.g. `Warning: skipped line 7 (invalid date): 2023-13-01,29.5,71,0.7` |
| CSV file missing or unreadable | Prints `Could not read weatherdata.csv: ...` and exits |

---

## AI Disclosure Note

### Prompt 1

Gave this prompt to CLAUDE to generate a CSV file with the necessary data columns and the appropriate data types. I did this so I could spend less time manually writing a large amount of data to make sure that my weather parser works.

The result was that I received a CSV file with the requested data as explained in the prompt. I did not specify what data the date column should be, and thus made it a LocalDate type. The file was easily parsable, and the data put into the CSV was generated from a fixed random seed, meaning the data is realistic, but it was not real weather data. I also received 8 months of data to parse so I can test the averages from different months.

I made a weatherdata.csv file in the root folder of the project and copied and pasted the data into that file. Here is the prompt I made:

> “create me a weatherdata.csv file that includes a date column (ex. 2023-08-01) in this format year/month/day, a temperature column with a double data type, a humidity column with an int data type and precipitation with a double data type”

### Prompt 2

Gave this prompt to CLAUDE in order to get a good footing on starting off with the project. I provided the prompt with very specific and detailed instructions on what the app should and should not use according to the assignment.

The result was CLAUDE asking me, step by step, for permission to add certain changes to my project. Such as deleting all 5 classes that were added by default to the project and replacing it with a classless main. It also gave me a console menu to use for interaction

> “Ok using modern java features such as java records, enhanced switch statements, text blocks, pattern matching for switch, lambdas, streams, and any other modern java features from java 15 - java 23, create me a weather data analyzer app that parses weather data from a csv file, gives the average temp for a specific month, gives days with temps above a given threshhold, gives the count of rainy days, uses an enhanced switch statement for weather categories like "hot", "warm" or "cold". it also uses lambdas and streams to parse data efficiently. The app must NOT use explicit classes and should use records, interfaces and functional constructs. Finally it must use markdown javadoc for all methods and features and must include code examples in the javadoc using the markdown support in java 18. My restrtictions: do not add anything extra that wasnt asked from these requirments, do not assume anything, and do not add, delete, and change anything without my permission.”

### Prompt 3

Gave this prompt to Claude to fix a bug after the previous prompt. When the user enters the wrong input, the program says “Wrong input. Please enter a number.” and goes back to the main menu. I think that would be pretty annoying having to go back to an option constantly.

So I told Claude to keep asking the user until a proper input is given or the user can press the X key to exit to the main menu. The result was exactly what was described; the user keeps looping until a proper answer is given or the X key is pressed. Here is my prompt:

> “something that doesn't make sense, if i enter a wrong input it should loop until i do enter a correct input or press a certain key to stop”

### Prompt 4

Gave this prompt to Claude to give me a detailed README.md file that includes the features the app has, the author's name, what is needed and how to run the application, and other details.

The result was a detailed read me file. It tried to put my GitHub name, so I had to change it.

> “Please generate me a detailed README.md file with all the applications features, what is needed to run the application, my name, how the csv file is supposed to be formatted in order for it to be read from and all the main java featured used in the application”

### Prompt 5

Gave this prompt to Claude to fix a logical problem in my README.md file. In the section about CSV data format, a section tells the user that they are able to use their own file if it has the data formatted exactly the same as the weatherdata.csv file. This was wrong, as the file would have to also be named weatherdata.csv, or they would have to copy and paste their data into the weatherdata.csv file themselves.

Thus, the readme file was updated to state that the user would need to replace the weatherdata.csv file with their own file(it also has to be named weatherdata.csv), or the user can replace all the data in the weatherdata.csv file as long as the data is formatted properly. Here is the prompt:

> “dont change anything, the file sais the reader can change the file with any other file with the same formatted data, is that true, how would they do it?”
