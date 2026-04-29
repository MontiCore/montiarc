/* (c) https://github.com/MontiCore/monticore */
package de.monticore.sd2arc.util;

import montiarc.util.Error;

/**
 * The enum of all SD2Arc errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xB5100 - 0xB5199
 */
public enum SD2ArcError implements Error {
  SUBCOMPONENT_NOT_EXISTS("0xB5100", "Subcomponent '%s' does not exist in the embedding component '%s'"),
  IMPLIED_CONNECTORS_FIT("0xB5101", "Implied connector from '%s' to '%s' does not fit"),
  OBSERVE_ON_UNCONNECTED_PORT("0xB5102", "Cannot observe on unconnected port '%s'");

  private final String errorCode;
  private final String errorMsgFormat;

  SD2ArcError(String errorCode, String errorMsgFormat) {
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
