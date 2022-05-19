/* (c) https://github.com/MontiCore/monticore */
package montiarc.util;

import de.monticore.scbasis._cocos.*;
import de.monticore.sctransitions4code._cocos.AnteBlocksOnlyForInitialStates;
import de.monticore.sctransitions4code._cocos.TransitionPreconditionsAreBoolean;

/**
 * Wraps statechart error messages into enum values, so they can be used in
 * combination with the existing test infrastructure
 */
public enum StatechartError implements Error {
  UNIQUE_STATES(UniqueStates.ERROR_CODE),
  TRANSITION_SOURCE_EXISTS(TransitionSourceTargetExists.SOURCE_ERROR_CODE),
  TRANSITION_TARGET_EXISTS(TransitionSourceTargetExists.TARGET_ERROR_CODE),
  CAPITAL_STATE_NAMES(CapitalStateNames.ERROR_CODE),
  PACKAGE_CORRESPONDS_TO_FOLDERS(PackageCorrespondsToFolders.ERROR_CODE),
  SC_FILE_EXTENSION(SCFileExtension.ERROR_CODE),
  SC_NAME_IS_ARTIFACT_NAME(SCNameIsArtifactName.ERROR_CODE),
  PRECONDITION_IS_NOT_BOOLEAN(TransitionPreconditionsAreBoolean.ERROR_CODE),
  MISSING_INITIAL_STATE(AtLeastOneInitialState.ERROR_CODE),
  ANTE_NOT_AT_INITIAL(AnteBlocksOnlyForInitialStates.ERROR_CODE);

  private final String errorCode;

  StatechartError(String errorCode) {
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
