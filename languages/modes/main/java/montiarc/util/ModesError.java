/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all montiarc mode automata errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1350 - 0xC1399
 */
public enum ModesError implements Error {
  MULTIPLE_MODE_AUTOMATA("0xC1350", "Components may only have one mode-automaton at max."),
  MODE_AUTOMATON_IN_ATOMIC_COMPONENT("0xC1351", "Atomic components may not define modes and mode automata."),
  MODE_AUTOMATON_CONTAINS_STATE("0xC1352", "Mode automata cannot define states only modes"),
  STATECHART_CONTAINS_MODE("0xC1353", "The behavior statechart cannot define modes"),
  MODE_CONTAINS_PORT_DEFINITION("0xC1354", "Port '%s' cannot be defined in mode");

  private final String errorCode;
  private final String errorMessage;

  ModesError(String errorCode, String errorMessage) {
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
