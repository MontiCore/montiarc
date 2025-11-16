/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all montiarc automaton errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1300 - 0xC1349
 */
public enum ArcAutomataError implements Error {
  MALFORMED_EXPRESSION("0xC1301", "The expression at '%s' is malformed and cannot be evaluated"),
  INPUT_PORT_IN_INITIAL_OUT_DECL("0xC1303", "Input port '%s' is referenced in the initial output declaration. This " +
      "is illegal, as input port values are undefined at the point of component initialization"),
  PORT_NOT_WRITTEN_IN_TRANSITION("0xC1304", "There is a transition, which, when followed, leaves port '%s' without a value"),
  PORT_NOT_WRITTEN_IN_STATE("0xC1305", "When staying in state '%s' (which might happen because there is no unconditional " +
      "transition leaving that state), port '%s' is left without a value"),
  MSG_EVENT_WITHOUT_SYMBOL("0xC1306", "Could not resolve a symbol for a message event"),
  STATECHART_ANTE_ACTION_NOT_SUPPORTED("0xC1307", "Statecharts ante actions are not supported. Use an initial state with entry action instead");
  private final String errorCode;
  private final String errorMsgFormat;

  ArcAutomataError(String errorCode, String errorMsgFormat) {
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
