/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;

import java.util.regex.Pattern;

import static montiarc._cocos.ForEachIsValid4MA.FOR_EACH_EXPR_NOT_ITERABLE_ERROR_CODE;
import static montiarc._cocos.ForEachIsValid4MA.FOR_EACH_EXPR_NOT_ITERABLE_ERROR_MSG;
import static montiarc._cocos.ForEachIsValid4MA.FOR_EACH_TYPE_MISMATCH_ERROR_CODE;
import static montiarc._cocos.ForEachIsValid4MA.FOR_EACH_TYPE_MISMATCH_ERROR_MSG;

/**
 * Wraps monticore error messages into enum values, so they can be used in
 * combination with the existing test infrastructure
 */
public enum MCError implements Error {
  TYPE_REF_ASSIGNMENT_ERR(VarDeclarationInitializationHasCorrectType.TYPE_REF_ASSIGNMENT_ERROR_CODE),
  INCOMPATIBLE_TYPE(VarDeclarationInitializationHasCorrectType.ERROR_CODE),
  DIVIDE_EXPRESSION_ERR("0xA0168"),
  CANT_FIND_SYMBOL("0xA0324"),
  CANT_FIND_SYMBOL_IN_EXPRESSION("0xFD118"),
  TYPE_STRING_NOT_RESOLVABLE("0xD02A6"),
  FOUND_MULTIPLE_SYMBOLS("0xA4095"),
  TARGET_TYPE_MISMATCH("0xFD451"),
  EXPRESSION_LVALUE("0xFDD47"),
  MISSING_COMPONENT("0xD0104"),
  FOR_EACH_EXPR_NOT_ITERABLE(FOR_EACH_EXPR_NOT_ITERABLE_ERROR_CODE),
  FOR_EACH_TYPE_MISMATCH(FOR_EACH_TYPE_MISMATCH_ERROR_CODE);

  public static final Pattern ERROR_CODE_PATTERN = Pattern.compile("0xA\\d{4}");

  private final String errorCode;

  MCError(String errorCode) {
    assert (errorCode != null);
    this.errorCode = errorCode;
  }

  @Override
  public String getErrorCode() {
    return errorCode;
  }

  @Override
  public String printErrorMessage() {
    throw new UnsupportedOperationException();
  }
}
