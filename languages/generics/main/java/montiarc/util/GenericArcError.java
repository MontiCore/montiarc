/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

/**
 * The enum of all montiarc generics errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC1200 - 0xC1249
 */
public enum GenericArcError implements Error {
  TYPE_PARAMETER_UPPER_CASE("0xC1201", "Type parameters should start with an uppercase letter"),
  TYPE_ARG_IGNORES_UPPER_BOUND("0xC1202", "Type parameter '%s' does not respect its upper bound, should extend '%s'"),
  TOO_FEW_TYPE_ARGUMENTS("0xC1203", "Too few type arguments, expected `%s` but provided `%s`"),
  TOO_MANY_TYPE_ARGUMENTS("0xC1204", "Too many type arguments, expected `%s` but provided `%s`"),
  HERITAGE_TYPE_ARG_IGNORES_UPPER_BOUND("0xC1205", "Type parameter '%s' does not respect its upper bound, should extend '%s'"),
  HERITAGE_TOO_FEW_TYPE_ARGUMENTS("0xC1206", "Too few type arguments, expected `%s` but provided `%s`"),
  HERITAGE_TOO_MANY_TYPE_ARGUMENTS("0xC1207", "Too many type arguments, expected `%s` but provided `%s`");

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
