/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;

import java.util.regex.Pattern;

/**
 * Wraps monticore error messages into enum values, so they can be used in
 * combination with the existing test infrastructure
 */
public enum MCError implements Error {
  INCOMPATIBILITY(VarDeclarationInitializationHasCorrectType.ERROR_CODE),
  TYPE_REF_ASSIGNMENT_ERR(VarDeclarationInitializationHasCorrectType.TYPE_REF_ASSIGNMENT_ERROR_CODE),
  DIVIDE_EXPRESSION_ERR("0xA0212");

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
