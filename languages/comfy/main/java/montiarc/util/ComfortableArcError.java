/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all comfortable arc errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1450 - 0xC1499
 */
public enum ComfortableArcError implements Error {

  MULTIPLE_AUTOCONNECTS("0xC1450", "There are %s autoconnect declarations in this component. Only 1 is allowed"),
  AUTOCONNECT_IN_ATOMIC_COMPONENT("0xC1451", "This autoconnect declaration is in an atomic component which is illegal"),
  CONNECTED_COMPONENT_CONNECTOR_SRC_HAS_COMP_NAME("0xC1452", "Source ports of connectors declared during component " +
    "instantiation may only reference the component instance they belong to. -> '%s' is disallowed.");

  private final String errorCode;
  private final String errorMessage;

  ComfortableArcError(String errorCode, String errorMessage) {
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
