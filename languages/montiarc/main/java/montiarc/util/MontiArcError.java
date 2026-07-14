/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all montiarc errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1000 - 0xC1099
 */
public enum MontiArcError implements Error {
  COMPONENT_AND_FILE_NAME_DIFFER("0xC1000", "The component name '%s' does not correspond to the file name '%s'"),
  PACKAGE_AND_FILE_PATH_DIFFER("0xC1001", "The package name '%s' does not correspond to the file path '%s'"),
  TOOL_PARSE_IOEXCEPTION("0xC1002", "Could not parse the file '%s'"),
  TOOL_FILE_WALK_IOEXCEPTION("0xC1003", "Could not access the directory '%s' or one of its subdirectories"),
  CLI_INPUT_OPTION_MISSING("0xC1004", "Option '%s' is missing, but an input is required"),
  CLI_INPUT_FILE_NOT_EXIST("0xC1005", "Input file '%s' does not exist"),
  CLI_OPTION_AMBIGUOUS("0xC1006", "Option '%s' does not match any valid option"),
  CLI_OPTION_UNRECOGNIZED("0xC1007", "Unrecognized option '%s'"),
  CLI_OPTION_MISSING("0xC1008", "Mandatory options [%s] are missing"),
  CLI_ARGUMENT_MISSING("0xC1009", "Option '%s' is missing an argument"),
  ROOT_NO_INSTANCE("0xC1010", "Cannot instantiate component without context"),
  SUPERIMPOSED_MODELPATH("0xC1011", "The path %s superimposes another filepath %s"),
  SUPERIMPOSED_SYMPATH("0xC1012", "The sympath %s superimposes another sympath %s"),
  UNIT_CANNOT_HAVE_PORTS("0xC1011", "Tests must be deployable and cannot have ports"),
  UNIT_MISSING_ARGUMENT("0xC1012", "Missing test assignment for argument '%s'"),
  UNIT_MISSING_ARGUMENTS("0xC1013", "Missing test assignments in test case %d for arguments '%s'"),
  UNIT_TOO_MANY_ARGUMENTS("0xC1014", "Too many assignments in test case %d"),
  UNIT_TEST_SOURCE_MISCONFIGURED("0xC1015", "Test source misconfigured, has to be a set of test cases in the form of <<test={}>>"),
  UNIT_TEST_CASE_MISCONFIGURED("0xC1016", "Test case %d misconfigured, has to be a list of parameter assignments in the form of <<test={[]}>>"),
  UNIT_TEST_CASE_PARAMETER_MISCONFIGURED("0xC1017", "Test case %d, parameter %d misconfigured, has to be a list of parameter assignments in the form of <<test={[1,2,3]}>>"),
  UNIT_TEST_SOURCE_AND_VALUE_SOURCE("0xC1018", "Cannot combine test and value source for parameter '%s', choose either <<test={[value]}>> or <<test, %<s=[value]>>, not both"),
  UNIT_DUPLICATE_ARGUMENTS("0xC1019", "Multiple test assignments found for argument '%s'"),
  UNIT_TYPE_MISMATCH("0xC1020", "Test assignment type mismatch for '%s', expected '%s' but provided '%s'"),
  UNIT_TEST_COUNT_MISMATCH("0xC1021", "Test count mismatch, expected values for '%d' tests but provided '%d'"),
  TOOL_SIMULATION_FAILED("0xC1022", "Simulation exited with error code %d. Output:\n%s"),
  TOOL_CREATE_TEMPLATE_NOT_EXIST("0xC1023", "The template '%s' does not exist"),
  IMPORTED_SYMBOL_MISSING("0xC1024", "Cannot resolve imported symbol '%s'"),
  BREAK_STATEMENT_TARGETS_NO_LOOP("0xC1025", "Break statements may only target loops.");

  private final String errorCode;
  private final String errorMsgFormat;

  MontiArcError(String errorCode, String errorMsgFormat) {
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
