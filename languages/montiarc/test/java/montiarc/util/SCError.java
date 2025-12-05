/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import de.monticore.scbasis._cocos.AtLeastOneInitialState;
import de.monticore.scbasis._cocos.CapitalStateNames;
import de.monticore.scbasis._cocos.MaxOneInitialState;
import de.monticore.scbasis._cocos.UniqueStates;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;

import static de.monticore.scbasis._cocos.TransitionSourceTargetExists.CANT_FIND_SOURCE_ERROR_CODE;
import static de.monticore.scbasis._cocos.TransitionSourceTargetExists.CANT_FIND_SOURCE_ERROR_MSG;
import static de.monticore.scbasis._cocos.TransitionSourceTargetExists.CANT_FIND_TARGET_ERROR_CODE;
import static de.monticore.scbasis._cocos.TransitionSourceTargetExists.CANT_FIND_TARGET_ERROR_MSG;

/**
 * Wraps statechart errors into enum values so that they can be used in combination with the existing testing
 * infrastructure
 */
public enum SCError implements Error {
  DUPLICATE_STATE(UniqueStates.ERROR_CODE, ""),
  MORE_THAN_ONE_INITIAL_STATE(MaxOneInitialState.ERROR_CODE, ""),
  MISSING_INITIAL_STATE(AtLeastOneInitialState.ERROR_CODE, ""),
  STATE_NAME_NOT_CAPITAL(CapitalStateNames.ERROR_CODE, ""),
  PRECONDITION_NOT_BOOLEAN(TransitionPreconditionsAreBoolean.ERROR_CODE, "Guard expressions must be boolean. Your guard expression is of type '%s'."),
  CANT_FIND_SOURCE(CANT_FIND_SOURCE_ERROR_CODE, CANT_FIND_SOURCE_ERROR_MSG),
  CANT_FIND_TARGET(CANT_FIND_TARGET_ERROR_CODE, CANT_FIND_TARGET_ERROR_MSG);

  private final String errorCode;
  private final String errorMsgFormat;

  SCError(String errorCode, String errorMsgFormat) {
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
