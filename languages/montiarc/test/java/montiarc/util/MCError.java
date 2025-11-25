/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;

/**
 * Wraps monticore error messages into enum values, so they can be used in
 * combination with the existing test infrastructure
 */
public enum MCError implements Error {
  TYPE_REF_ASSIGNMENT_ERR(VarDeclarationInitializationHasCorrectType.TYPE_REF_ASSIGNMENT_ERROR_CODE, ""),
  INCOMPATIBLE_TYPE(VarDeclarationInitializationHasCorrectType.ERROR_CODE, ""),
  DIVIDE_EXPRESSION_ERR("0xA0168", ""),
  CANT_FIND_SYMBOL("0xA0324", ""),
  CANT_FIND_SYMBOL_IN_EXPRESSION("0xFD118", ""),
  TYPE_STRING_NOT_RESOLVABLE("0xD02A6", ""),
  FOUND_MULTIPLE_SYMBOLS("0xA4095", ""),
  TARGET_TYPE_MISMATCH("0xFD451", ""),
  EXPRESSION_LVALUE("0xFDD47", ""),
  MISSING_COMPONENT("0xD0104", "Cannot resolve component '%s'");

  private final String errorCode;
  private final String errorMsgFormat;

  MCError(String errorCode, String errorMsgFormat) {
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
    return this.errorCode + " " + this.getErrorMsgFormat();
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
