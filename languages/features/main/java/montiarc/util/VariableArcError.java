/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all montiarc variable errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1400 - 0xC1449
 */
public enum VariableArcError implements Error {
  CONSTRAINT_EXPRESSION_WRONG_TYPE("0xC1400", "Incompatible types: '%s' cannot be converted to 'boolean'"),
  CONSTRAINT_NOT_SATISFIED("0xC1401", "Constraints are not satisfied"),
  FEATURE_UPPER_CASE("0xC1402", "Convention violation, features should be lower case"),
  FEATURE_UNUSED("0xC1403", "Feature '%s' is never used"),
  SUBCOMPONENTS_NOT_CONSTRAINT("0xC1404", "Features %s are not correctly constraint by this component"),
  IF_STATEMENT_EXPRESSION_WRONG_TYPE("0xC1405", "Incompatible types: '%s' cannot be converted to 'boolean'"),
  FIELD_REFERENCE_IN_IF_STATEMENT_ILLEGAL("0xC1415", "Value of field '%s' not available in static context"),
  FIELD_REFERENCE_IN_CONSTRAINT_ILLEGAL("0xC1416", "Value of field '%s' not available in static context"),
  EXPRESSION_NOT_SMT_CONVERTIBLE("0xC1417", "Unsupported subexpression '%s': %s");

  private final String errorCode;
  private final String errorMsgFormat;

  VariableArcError(String errorCode, String errorMsgFormat) {
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
