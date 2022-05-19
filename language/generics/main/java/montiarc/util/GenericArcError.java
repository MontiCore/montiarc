/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all montiarc generics errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1200 - 0xC1249
 */
public enum GenericArcError implements Error {
  TYPE_PARAMETER_UPPER_CASE_LETTER("0xC1201", "The generic type parameter '%s' of component '%s'"
      + " should start with an upper case letter."),
  TYPE_ARG_IGNORES_UPPER_BOUND("0xC1202", "Type argument '%s' does not respect upper bound '%s' for type parameter " +
      "'%s' of component type component type"),
  TOO_FEW_TYPE_ARGUMENTS("0xC1203", "There are '%d' type arguments for component type '%s', " +
      "but has '%d' mandatory type parameters that all must be bound."),
  TOO_MANY_TYPE_ARGUMENTS("0xC1204", "There are '%d' type arguments for component type '%s', " +
      "but it only has '%d' type parameters. Please do not provide more arguments than type parameters exist.");

  private final String errorCode;
  private final String errorMessage;

  GenericArcError(String errorCode, String errorMessage) {
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
