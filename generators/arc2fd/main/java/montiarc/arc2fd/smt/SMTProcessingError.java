/* (c) https://github.com/MontiCore/monticore */
package montiarc.arc2fd.smt;

import montiarc.util.Error;

/**
 * The enum of all montiarc automaton errors, which extends the mixing
 * interface {@link Error}.
 * <p>
 * Assigned code range: 0xC8220 - 0xC8249
 */
public enum SMTProcessingError implements Error {
  CNF_CONVERSION_NO_FORMULA_FOUND("0xC8220", "No Formula found for " +
      "CNF-Conversion"),
  CNF_CONVERSION_FAILED_CONVERTING_TO_NNF("0xC8221", "An error occurred while" +
      " converting the given formula (%s) to CNF. The intermediate " +
      "conversion to NNF failed!"),
  CNF_CONVERSION_FAILED("0xC8222", "Converting the formula (%s) into CNF " +
      "failed due to unknown reasons. The converted formula is (%s)"),
  ANALYSIS_NO_FORMULA_FOUND("0xC8223", "No Formula found for Analysis"),
  SMT2FD_PROCESSING_NO_FORMULA("0xC8224", "No Formula found for Processing " +
      "inside SMT2FDVisitor!"),
  SMT2FD_VISITOR_NO_CNF("0xC8225", "For SMT2FD Processing the formula must be" +
      " in CNF, but '%s' is no CNF!"),
  ROOT_IS_NULL("0xC8226", "Root is null! In order to process a formula '%s' " +
      "with SMT2FD, the root must not be null."),
  FORMULA_ANALYZER_QUANTIFIER_NOT_SUPPORTED("0xC8230", "Found a Quantifier " +
      "while analyzing (Quantified AST = '%s', Body = '%s'). Quantifiers " +
      "are not supported!");

  private final String errorCode;

  private final String errorMessage;

  SMTProcessingError(String errorCode, String errorMessage) {
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
