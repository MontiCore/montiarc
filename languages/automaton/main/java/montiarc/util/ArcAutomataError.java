/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all montiarc automaton errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1400 - 0xC1449
 */
public enum ArcAutomataError implements Error {
  MALFORMED_EXPRESSION("0xC1401", "The expression at '%s' is malformed and can not be evaluated."),
  MANY_INITIAL_STATES("0xC1402",
      "Automata may not have more than one initial state, but the one of %s has %d: %s and %s"),
  INPUT_PORT_IN_INITIAL_OUT_DECL("0xC1403", "Input port '%s' is referenced in the initial output declaration. This " +
      "is illegal as input port values are undefined at the point of component initialization."),
  PORT_NOT_WRITTEN_IN_TRANSITION("0xC1404", "There is a transition, which, when followed, leaves port '%s' without a value."),
  PORT_NOT_WRITTEN_IN_STATE("0xC1405", "When staying in state '%s' (which might happen, because there is no unconditional" +
      "transition leaving that state), port '%s' is left without a value");

  private final String errorCode;
  private final String errorMessage;

  ArcAutomataError(String errorCode, String errorMessage) {
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
