/* (c) https://github.com/MontiCore/monticore */
package scmapping.util;

import montiarc.util.Error;

/**
 * The enum of all Statechart Conformance checking errors,
 * which extends the mixing interface {@link Error}.
 * <p>
 * Assigned code range: 0xC2000 - 0xC2099
 */
public enum ConformanceError implements Error {
  VALUE_RIGHT_IN_EQUAL_EXPRESSIONS("0xC2001", "Invalid expression \"%s\" at position %s. The left side of EqualsExpression can either be a state, input-port, output-port or global variable. Values must be at the right side"),
  VALUE_RIGHT_IN_NOT_EQUAL_EXPRESSIONS("0xC2002", "Invalid expression \"%s\" at position %s. The left side of EqualsExpression can either be a state, input-port, output-port or global variable. Values must be at the right side"),
  INVALID_NAME_ALTERNATIVES("0xC2003", "Invalid expression \"%s\" at position %s. The alternatives %s could be. Note: all names relative to the concrete Statecharts must be on the left side of rules and names relative to the reference statecharts must be on the right side");

  private final String errorCode;
  private final String errorMsgFormat;

  ConformanceError(String errorCode, String errorMsgFormat) {
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
