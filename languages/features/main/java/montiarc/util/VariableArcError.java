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
  FEATURE_UPPER_CASE("0xC1402", "Feature names should start with a lower case letter"),
  FEATURE_UNUSED("0xC1403", "Feature '%s' is never used"),
  IF_STATEMENT_EXPRESSION_WRONG_TYPE("0xC1404", "Incompatible types: '%s' cannot be converted to 'boolean'"),
  ILLEGAL_USE("0xC1405", "The element '%s' of component '%s' cannot be used in the context of '%s'"),
  NAMED_ARGUMENTS_LAST("0xC1407", "Ordered arguments must be declared before named parameters, " +
    "but parameter at position '%d' is preceded by the named argument at position '%d'"),
  TOO_MANY_INSTANTIATION_FEATURE_ARGUMENTS("0xC1408", "Provided '%d' feature arguments but expected no more than '%d'"),
  NAMED_ARGUMENT_NO_NAME_LEFT("0xC1409", "Expected a named parameter at argument position '%d'"),
  NAMED_ARGUMENT_FEATURE_NOT_EXIST("0xC1410", "Cannot resolve feature symbol '%s'"),
  NAMED_ARGUMENT_EXPRESSION_WRONG_TYPE("0xC1411", "Incompatible types: '%s' cannot be converted to 'boolean'"),
  PORT_REFERENCE_IN_IF_STATEMENT_ILLEGAL("0xC1413", "Messages are an element of the runtime, cannot read from port '%s' at design-time"),
  PORT_REFERENCE_IN_CONSTRAINT_ILLEGAL("0xC1414", "Messages are an element of the runtime, cannot read from port '%s' at design-time"),
  FIELD_REFERENCE_IN_IF_STATEMENT_ILLEGAL("0xC1415", "Variables are an element of the runtime, cannot use field '%s' at design-time"),
  FIELD_REFERENCE_IN_CONSTRAINT_ILLEGAL("0xC1416", "Variables are an element of the runtime, cannot use field '%s' at design-time");

  private final String errorCode;
  private final String errorMessage;

  VariableArcError(String errorCode, String errorMessage) {
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
