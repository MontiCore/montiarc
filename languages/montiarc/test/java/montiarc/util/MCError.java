/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;

/**
 * Wraps monticore error messages into enum values, so they can be used in
 * combination with the existing test infrastructure
 */
public enum MCError implements Error {
  INCOMPATIBLE_TYPE(VarDeclarationInitializationHasCorrectType.ERROR_CODE, ""),
  DIVIDE_EXPRESSION_ERR("0xA0168", ""),
  CANT_FIND_SYMBOL("0xA0324", "Cannot find symbol %s"),
  CANT_FIND_SYMBOL_IN_EXPRESSION("0xFD118", "could not find symbol for expression \"%s\""),
  TYPE_STRING_NOT_RESOLVABLE("0xD02A6", ""),
  FOUND_MULTIPLE_SYMBOLS("0xA4095", ""),
  TARGET_TYPE_MISMATCH("0xFD451", ""),
  EXPRESSION_LVALUE("0xFDD47", ""),
  EXPR_ASSIGNMENT_INCOMPATIBLE_TYPES("0xA0179", ""),
  EXPR_LOGICAL_COMP_ASSIGNMENT_OP_NOT_APPLICABLE("0xA0176", ""),
  EXPR_NUMERIC_COMP_ASSIGNMENT_OP_NOT_APPLICABLE("0xA0178", ""),
  EXPR_SHIFT_COMP_ASSIGNMENT_OP_NOT_APPLICABLE("0xA0177", ""),
  EXPR_BINARY_OP_NOT_APPLICABLE("0xC0203", ""),
  EXPR_LOGICAL_OP_NOT_APPLICABLE("0xB0113", ""),
  EXPR_NUMERICAL_OP_NOT_APPLICABLE("0xB0163", ""),
  EXPR_EQUAL_OP_NOT_APPLICABLE("0xB0166", ""),
  EXPR_NUMERIC_COMPARISON_OP_NOT_APPLICABLE("0xB0167", ""),
  EXPR_TERNARY_OP_CONDITION_NOT_BOOLEAN("0xB0165", ""),
  EXPR_NUMERIC_AFFIX_OP_NOT_APPLICABLE("0xA0184", ""),
  EXPR_UNARY_OP_NOT_APPLICABLE("0xA017D", ""),
  EXPR_BITWISE_NOT_NOT_APPLICABLE("0xB0175", ""),
  EXPR_LOGICAL_NOT_NOT_APPLICABLE("0xB0164", ""),
  EXPR_SHIFT_OP_NOT_APPLICABLE("0xC0201", ""),
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
