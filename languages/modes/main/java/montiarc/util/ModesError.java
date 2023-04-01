/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all montiarc mode automata errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1350 - 0xC1399
 */
public enum ModesError implements Error {
  INSTANCE_NAME_NOT_UNIQUE_IN_MODE("0xC1350", "There are multiple subcomponents named '%s' in mode '%s'."),
  COMPONENT_NAME_NOT_UNIQUE_IN_MODE("0xC1351", "There are multiple components named '%s' in mode '%s'."),
  PORT_NAME_NOT_UNIQUE_IN_MODE("0xC1352", "There are multiple ports named '%s' in mode '%s'."),
  HIERARCHICAL_MODE_ELEMENTS("0xC1353", "Hierarchical modes are not allowed."),
  MULTIPLE_MODE_AUTOMATA("0xC1354", "Components may only have one mode-automaton at max."),
  MODES_WITHOUT_AUTOMATON("0xC1355", "The component '%s' defines modes, but no mode-automaton."),
  MODE_ELEMENTS_IN_ATOMIC_COMPONENTS("0xC1356", "Atomic components may not define modes and mode automata."),
  INITIAL_MODE_DOES_NOT_EXIST("0xC1357", "The initial mode '%s' is not defined anywhere in the component '%s'.");

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
