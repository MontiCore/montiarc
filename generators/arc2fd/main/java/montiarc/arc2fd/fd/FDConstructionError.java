/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.fd;

import montiarc.util.Error;

/**
 * The enum of all montiarc automaton errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC8250 - 0xC8269
 */
public enum FDConstructionError implements Error {
  NO_CNF("0xC8250", "When using Disjunction2String the formula must be in CNF" +
    ". The given formula '%s' is not in CNF!"),
  NO_CONJUNCTIONS_ALLOWED("0xC8251", "When using Disjunction2String the " +
    "formula may not contain any conjunctions, but the given formula " +
    "'%s' has some!"),
  AST_IS_NULL("0xC8252", "AST couldn't be converted to FD. The given AST is " +
    "null!"),
  FORMULA_IS_UNSAT("0xC8253", "The Formula (%s) is not satisfiable! Please " +
    "rework the constraints in order to construct a useful Feature " +
    "Diagram!"),
  TYPE_HAS_NO_NAME_MAPPING("0xC8254", "There is no name mapping for the Type " +
    "(%s). Please revisit the FD-Configuration and provide proper " +
    "Mapping for all types."),
  NO_ASTS_FOUND("0xC8255", "No ASTs (.arc-Files) were found in the given path" +
    " (%s)"),
  ERROR_CHECKING_SAT("0xC2856", "Error while checking the formula (%s) for " +
    "Satisfiability."),
  UNSUPPORTED_KEY_TYPE("0xC2857", "The key type '%s' is not supported for " +
    "FDConstructionStorage!"),
  DUPLICATED_FEATURE("0xC2858", "The feature '%s' already appeared in another" +
    " (possibly nested) Component. " +
    "This will very likely lead to errors in the corresponding feature " +
    "diagram. Therefore, please revise the component definitions."),
  STORAGE_EMPTY("0xC2859", "The storage is empty after the FD-Construction. " +
    "It is very likely that an unexpected error occurred."),
  TRY_TO_MERGE_TWO_EMPTY_STORAGES("0xC2860", "Tried to combine two empty " +
    "StorageCaches.");

  private final String errorCode;

  private final String errorMessage;

  FDConstructionError(String errorCode, String errorMessage) {
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
