/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all arc basis errors. Implements the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1550 - 0xC1599
 */
public enum ArcComputeError implements Error {
  INIT_BLOCK_WITHOUT_COMPUTE("0xC1550", "'init' blocks are only allowed in combination with a 'compute' behavior"),
  MULTIPLE_INIT("0xC1551", "Multiple conflicting init behaviors");

  private final String errorCode;
  private final String errorMessage;

  ArcComputeError(String errorCode, String errorMessage) {
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
    return this.getErrorCode() + ": " + this.printErrorMessage();
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
