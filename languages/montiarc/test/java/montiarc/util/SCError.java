/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import de.monticore.scbasis._cocos.AtLeastOneInitialState;
import de.monticore.scbasis._cocos.MaxOneInitialState;
import de.monticore.scbasis._cocos.UniqueStates;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;

import java.util.regex.Pattern;

/**
 * Wraps statechart errors into enum values so that they can be used in combination with the existing testing
 * infrastructure
 */
public enum SCError implements Error {
  MORE_THAN_ONE_INITIAL_STATE(MaxOneInitialState.ERROR_CODE),
  NO_INITIAL_STATE(AtLeastOneInitialState.ERROR_CODE),
  DUPLICATE_STATE(UniqueStates.ERROR_CODE),
  PRECONDITION_NOT_BOOLEAN(TransitionPreconditionsAreBoolean.ERROR_CODE);

  public static final Pattern ERROR_CODE_PATTERN = Pattern.compile("0xCC\\d{3}");

  private final String errorCode;

  SCError(String errorCode) {
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
