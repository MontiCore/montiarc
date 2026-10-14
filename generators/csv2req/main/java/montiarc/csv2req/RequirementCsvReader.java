/* (c) https://github.com/MontiCore/monticore */
package montiarc.csv2req;

import com.google.common.base.Preconditions;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.codehaus.commons.nullanalysis.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Reads the IDs of requirements from CSV files (RFC 4180) of the form
 * <pre>
 * ID,Title,Text,Type,Priority,Source,Status
 * REQ-001,Example requirement,The system shall do something.,Functional,High,Stakeholder,Draft
 * </pre>
 * The values are separated by a configurable delimiter, a comma by default.
 * Parsing is delegated to Apache Commons CSV.
 * <p>
 * The header row is mandatory. The ID column is identified by its
 * (case-insensitive) header name. Multiple candidate names may be given, the
 * first candidate present in the header is used. All other columns are
 * ignored. Values may be quoted to contain delimiters, line breaks, or escaped
 * quotes ({@code ""}). A quote within an unquoted value is read literally.
 * <p>
 * The IDs are sanitized to match the MontiCore token {@code Name}, e.g.,
 * {@code REQ-001} becomes {@code REQ_001}.
 */
public class RequirementCsvReader {

  public static final String DEFAULT_ID_COLUMN = "ID";

  public static final char DEFAULT_DELIMITER = ',';

  protected final List<String> idColumns;

  protected final char delimiter;

  public RequirementCsvReader() {
    this(List.of(DEFAULT_ID_COLUMN));
  }

  /**
   * @param idColumns the candidate header names of the column containing the
   *                  requirement IDs, in the order of their precedence
   */
  public RequirementCsvReader(@NotNull List<String> idColumns) {
    this(idColumns, DEFAULT_DELIMITER);
  }

  /**
   * @param idColumns the candidate header names of the column containing the
   *                  requirement IDs, in the order of their precedence
   * @param delimiter the character separating the values, see {@link #isValidDelimiter(char)}
   */
  public RequirementCsvReader(@NotNull List<String> idColumns, char delimiter) {
    Preconditions.checkNotNull(idColumns);
    Preconditions.checkArgument(!idColumns.isEmpty());
    Preconditions.checkArgument(idColumns.stream().noneMatch(String::isBlank));
    Preconditions.checkArgument(isValidDelimiter(delimiter));
    this.idColumns = idColumns.stream().map(String::trim).toList();
    this.delimiter = delimiter;
  }

  public List<String> getIdColumns() {
    return this.idColumns;
  }

  public char getDelimiter() {
    return this.delimiter;
  }

  /**
   * @return whether the character can separate values, i.e., it is neither the
   * quote character nor a line break
   */
  public static boolean isValidDelimiter(char delimiter) {
    return delimiter != '"' && delimiter != '\r' && delimiter != '\n';
  }

  /**
   * A parsed CSV row together with the line it starts in.
   */
  protected record Row(int line, @NotNull List<String> values) {

    boolean isBlank() {
      return values.stream().allMatch(String::isBlank);
    }
  }

  /**
   * Reads the requirement IDs of the given CSV file. Malformed rows are
   * reported as errors and skipped.
   *
   * @param csv the CSV file to read
   * @return the sanitized IDs in the order of their appearance, or empty if
   * the file as a whole is malformed
   */
  public Optional<List<String>> read(@NotNull Path csv) {
    Preconditions.checkNotNull(csv);
    return read(FileReaderWriter.readFromFile(csv), csv.toString());
  }

  /**
   * Reads the requirement IDs of the given CSV content. Malformed rows are
   * reported as errors and skipped.
   *
   * @param content the CSV content
   * @param origin  the origin of the content used in error messages
   * @return the sanitized IDs in the order of their appearance, or empty if
   * the content as a whole is malformed
   */
  public Optional<List<String>> read(@NotNull String content, @NotNull String origin) {
    Preconditions.checkNotNull(content);
    Preconditions.checkNotNull(origin);

    Optional<List<Row>> parsed = parse(content, origin);
    if (parsed.isEmpty()) {
      return Optional.empty();
    }
    List<Row> rows = parsed.get().stream().filter(row -> !row.isBlank()).toList();
    if (rows.isEmpty()) {
      Log.error(CSV2ReqError.EMPTY_FILE.format(origin));
      return Optional.empty();
    }

    Row header = rows.get(0);
    Optional<Integer> idColumn = this.indexOfIdColumn(header);
    if (idColumn.isEmpty()) {
      Log.error(CSV2ReqError.MISSING_COLUMN.format(origin, this.printIdColumns()));
      return Optional.empty();
    }

    List<String> ids = new ArrayList<>();
    Map<String, Integer> definedIn = new HashMap<>();
    for (Row row : rows.subList(1, rows.size())) {
      if (row.values().size() != header.values().size()) {
        Log.error(CSV2ReqError.WRONG_COLUMN_COUNT.format(
          row.line(), origin, row.values().size(), header.values().size()));
        continue;
      }
      String id = row.values().get(idColumn.get()).trim();
      if (id.isEmpty()) {
        Log.error(CSV2ReqError.MISSING_ID.format(row.line(), origin));
        continue;
      }
      String name = sanitize(id);
      if (definedIn.containsKey(name)) {
        Log.error(CSV2ReqError.DUPLICATE_ID.format(id, name, row.line(), origin, definedIn.get(name)));
      } else {
        definedIn.put(name, row.line());
        ids.add(name);
      }
    }
    return Optional.of(ids);
  }

  /**
   * Sanitizes the given string to match the MontiCore token
   * {@code Name = ('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')*}.
   * Invalid characters are replaced by {@code _}, and a leading digit is
   * prefixed with {@code _}.
   *
   * @param s a non-empty string
   * @return the sanitized string
   */
  public static String sanitize(@NotNull String s) {
    Preconditions.checkNotNull(s);
    Preconditions.checkArgument(!s.isEmpty());
    String name = s.replaceAll("[^a-zA-Z0-9_$]", "_");
    return Character.isDigit(name.charAt(0)) ? "_" + name : name;
  }

  /**
   * @return the index of the first candidate ID column present in the header
   */
  protected Optional<Integer> indexOfIdColumn(@NotNull Row header) {
    List<String> names = header.values().stream()
      .map(name -> name.trim().toLowerCase(Locale.ROOT))
      .toList();
    return this.getIdColumns().stream()
      .map(column -> names.indexOf(column.toLowerCase(Locale.ROOT)))
      .filter(index -> index >= 0)
      .findFirst();
  }

  protected String printIdColumns() {
    return this.getIdColumns().stream().map(column -> "'" + column + "'").collect(Collectors.joining(", "));
  }


  /**
   * Splits the content into rows and values according to RFC 4180, using the
   * configured delimiter.
   *
   * @return the rows, or empty if the content is malformed
   */
  protected Optional<List<Row>> parse(@NotNull String content, @NotNull String origin) {
    // Spreadsheet applications tend to prefix UTF-8 exports with a byte order mark
    if (content.startsWith("﻿")) {
      content = content.substring(1);
    }

    // Empty lines are not ignored by the parser, but skipped as blank rows, such that line numbers can be tracked
    CSVFormat format = CSVFormat.RFC4180.builder()
      .setDelimiter(this.getDelimiter())
      .setIgnoreEmptyLines(false)
      .get();

    List<Row> rows = new ArrayList<>();
    try (CSVParser parser = CSVParser.parse(content, format)) {
      Iterator<CSVRecord> records = parser.iterator();
      long rowStart = 1;
      while (true) {
        CSVRecord record;
        try {
          if (!records.hasNext()) {
            break;
          }
          record = records.next();
        } catch (UncheckedIOException e) {
          Log.error(CSV2ReqError.MALFORMED_CSV.format(origin, rowStart, e.getCause().getMessage()));
          return Optional.empty();
        }
        rows.add(new Row(Math.toIntExact(rowStart), record.toList()));
        rowStart = parser.getCurrentLineNumber() + 1;
      }
    } catch (IOException e) {
      Log.error(CSV2ReqError.MALFORMED_CSV.format(origin, 1, e.getMessage()));
      return Optional.empty();
    }
    return Optional.of(rows);
  }
}
