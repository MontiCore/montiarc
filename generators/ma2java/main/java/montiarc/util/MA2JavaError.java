/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all montiarc to java generator errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0x9000 - 0xC9049
 */
public enum MA2JavaError implements Error {

  POST_GENERATION_FORMATTING_FAIL("0xC9000", " Could not format '%s' produced by template '%s' on '%s'.");

  private final String errorCode;
  private final String errorMsgFormat;

  MA2JavaError(String errorCode, String errorMsgFormat) {
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
    return errorCode;
  }

  /**
   * @return The error message of this error.
   */
  @Override
  public String getErrorMsgFormat() {
    return errorMsgFormat;
  }

  @Override
  public String toString() {
    return this.getErrorCode() + ": " + this.getErrorMsgFormat();
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
