/* (c) https://github.com/MontiCore/monticore */
package montiarc.csv2req;

import montiarc.util.Error;

/**
 * The enum of all CSV2Req errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xB5200 - 0xB5299
 */
public enum CSV2ReqError implements Error {
  CLI_PARSE("0xB5200", "Could not parse the command line arguments: %s"),
  INPUT_NOT_FOUND("0xB5201", "The input path '%s' does not exist"),
  READ_FAILED("0xB5202", "Could not read '%s': %s"),
  WRITE_FAILED("0xB5203", "Could not write '%s': %s"),
  EMPTY_FILE("0xB5204", "The CSV file '%s' is empty, expected a header row"),
  MISSING_COLUMN("0xB5205", "The CSV file '%s' has none of the ID columns %s"),
  MALFORMED_CSV("0xB5206", "The CSV file '%s' is malformed in the row starting in line %d: %s"),
  WRONG_COLUMN_COUNT("0xB5207", "Line %d of '%s' has %d columns, expected %d"),
  MISSING_ID("0xB5208", "Line %d of '%s' has no requirement ID"),
  DUPLICATE_ID("0xB5209", "Requirement ID '%s' (sanitized to '%s') in line %d of '%s' is already defined in line %d"),
  MISSING_OUTPUT("0xB5210", "No output directory for the symbol tables specified"),
  MISSING_INPUT("0xB5211", "No input CSV files or directories specified"),
  NOT_A_CSV_FILE("0xB5212", "The input file '%s' is not a CSV file (*.csv)"),
  INVALID_FILE_NAME("0xB5213", "The CSV file '%s' has no name besides its extension"),
  DUPLICATE_ARTIFACT("0xB5214", "The CSV files '%s' and '%s' both translate to the requirement artifact '%s'"),
  READ_ARGUMENT_FILE_FAILED("0xB5215", "Could not read the argument file '%s': %s");

  private final String errorCode;
  private final String errorMsgFormat;

  CSV2ReqError(String errorCode, String errorMsgFormat) {
    assert (errorCode != null);
    assert (errorMsgFormat != null);
    assert (ERROR_CODE_PATTERN.matcher(errorCode).matches());
    this.errorCode = errorCode;
    this.errorMsgFormat = errorMsgFormat;
  }

  /**
   * @return The unique error code of this error.
   */
  @Override
  public String getErrorCode() {
    return this.errorCode;
  }

  /**
   * @return The error message of this error.
   */
  @Override
  public String getErrorMsgFormat() {
    return this.errorMsgFormat;
  }

  @Override
  public String toString() {
    return this.errorCode + ": " + this.getErrorMsgFormat();
  }

  /**
   * Calls {@link String#format(String, Object...)} with this error message as template
   *
   * @param args arguments for the format-call. The number of arguments has to
   *             match the string defined in {@link #getErrorMsgFormat()}
   * @return properly formatted error message
   */
  public String format(Object... args) {
    return String.format(toString(), args);
  }
}
