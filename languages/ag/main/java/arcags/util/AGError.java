/* (c) https://github.com/MontiCore/monticore */
package arcags.util;

import montiarc.util.Error;

/**
 * The enum of all Assumption Guarantee variable errors, which extends the mixing
 * interface {@link montiarc.util.Error}.
 * <p>
 * Assigned code range: 0xC1600 - 0xC1649
 */
public enum AGError implements Error {
  CONDITION_EXPRESSION_WRONG_TYPE("0xC1650", "Incompatible types for %s: '%s' cannot be converted to 'boolean'.");

  private final String errorCode;
  private final String errorMsgFormat;

  AGError(String errorCode, String errorMsgFormat) {
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
