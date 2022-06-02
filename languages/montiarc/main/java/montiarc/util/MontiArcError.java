/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all montiarc errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1000 - 0xC1099
 */
public enum MontiArcError implements Error {
  COMPONENT_AND_FILE_NAME_DIFFER("0xC1000", "The component name '%s' does not correspond to the file name '%s'."),
  COMPONENT_AND_FILE_PACKAGE_DIFFER("0xC1001", "The package name '%s' does not correspond to the file path '%s'."),
  TOOL_PARSE_IOEXCEPTION("0xC1002", "Could not parse the file \" %s \"."),
  TOOL_FILE_WALK_IOEXCEPTION("0xC1003", "Could not access the directory \" %s \" or one of its subdirectories."),
  CLI_INPUT_OPTION_MISSING("0xC1004", "Option '%s' is missing, but an input is required"),
  CLI_INPUT_FILE_NOT_EXIST("0xC1069", "Input file '%s' does not exist\n"),
  CLI_OPTION_AMBIGUOUS("0xC1006", "Option '%s' does not match any valid option"),
  CLI_OPTION_UNRECOGNIZED("0xC1007", "Unrecognized option '%s'"),
  CLI_OPTION_MISSING("0xC1008", "Options [%s] are missing, but are required"),
  CLI_ARGUMENT_MISSING("0xC1009", "Option '%s' is missing an argument");

  private final String errorCode;
  private final String errorMessage;

  MontiArcError(String errorCode, String errorMessage) {
    assert (errorCode != null);
    assert (errorMessage != null);
    assert (ERROR_CODE_PATTERN.matcher(errorCode).matches());
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
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
  public String printErrorMessage() {
    return this.errorMessage;
  }

  @Override
  public String toString() {
    return this.errorCode + ": " + this.printErrorMessage();
  }

  /**
   * Calls {@link String#format(String, Object...)} with this error message as template
   *
   * @param args arguments for the format-call. The number of arguments has to
   *             match the string defined in {@link #printErrorMessage()}
   * @return properly formatted error message
   */
  public String format(Object... args) {
    return String.format(toString(), args);
  }
}