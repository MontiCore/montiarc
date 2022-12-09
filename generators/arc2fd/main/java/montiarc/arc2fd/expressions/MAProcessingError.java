/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.expressions;

import montiarc.util.Error;

/**
 * The enum of all montiarc automaton errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC8200 - 0xC8219
 */
public enum MAProcessingError implements Error {
  NO_FORMULA_FOUND("0xC8213", "Problems occurred while converting the formula" +
    ". Tried to get the last formula of the array, but formula array is" +
    " empty."),
  UNSUPPORTED_DECIMAL_DATATYPE("0xC8201", "The given datatype '%s' is " +
    "currently not supported! The value was converted into a long"),
  ERROR_NOT_ENOUGH_FORMULAS("0xC8202", "Problems occurred while converting " +
    "the formula. Expected %s formulas to combine, but only %s Formulas" +
    " was found"),
  ERROR_WHILE_CONVERTING_FORMULA("0xC8203", "Problems occurred while " +
    "converting the formula. Most likely the conversion failed because " +
    "the types didn't match the expected ones.\n" +
    "The error appeared for the operation (%s) and the current formula " +
    "buffer is %s.\n" +
    "The formulas that have been tried to combine are: %s"),
  FORMULA_MIGHT_BE_CORRUPTED("0xC8204", "The Created Formula is likely to be " +
    "corrupted! The current formula stack is '%s'"),
  INTEGER_OPERATION_WITHOUT_INTEGER_FORMULA("0xC8205", "Tried to combine " +
    "Formulas ('%s', '%s') by the Integer Operation (%s), but only one " +
    "of the Formulas is actually a Integer Formula. " +
    "The FD-Construction will still work, but is still suggested to " +
    "change by modifying the Constraints."),
  CONVERTED_TO_LONG("0xC8206", "Floating point datatype ('%s') are currently " +
    "not supported. The number was automatically casted into an long."),
  EXPRESSION_TYPE_NOT_SUPPORTED_FOR_CONVERSION("0xC8207", "Problems occurred " +
    "while converting formula: The datatype '%s' is currently not " +
    "supported!"),
  SMT2FD_VISITOR_NO_CNF("0xC8208", "When using SMT2FDVisitor, the formula " +
    "must be in CNF. The given formula '%s' is not in CNF!");


  private final String errorCode;

  private final String errorMessage;

  MAProcessingError(String errorCode, String errorMessage) {
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
   * Calls {@link String#format(String, Object...)} with this error message
   * as template
   *
   * @param args arguments for the format-call. The number of arguments has to
   *             match the string defined in {@link #printErrorMessage()}
   * @return properly formatted error message
   */
  public String format(Object... args) {
    return String.format(toString(), args);
  }
}
