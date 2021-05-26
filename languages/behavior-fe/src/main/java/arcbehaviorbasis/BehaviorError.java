/* (c) https://github.com/MontiCore/monticore */
package arcbehaviorbasis;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;
import montiarc.util.Error;

public enum BehaviorError implements Error {

  MULTIPLE_BEHAVIOR("RRW14 37/40",
      "The component type %s defines multiple behaviors, but only one is allowed at max"),
  BEHAVIOR_IN_COMPOSED_COMPONENT("",
      "Only atomic components may have behavior specifications, but %s is composed."),
  ONE_INITIAL_STATE("RRW14 31 / Wor16 99",
      "All automata must have exactly one initial state, but the one of %s has %s"),
  ONE_INITIAL_IN_HIERARCHICAL("",
      "An hierarchical state should have exactly one initial state, but one in %s has %s"),
  FIELD_IN_GUARD_MISSING("RRW14 32", ""),
  FIELD_IN_ACTION_MISSING("RRW14 32", "");

  BehaviorError(String code, String message) {
    this.message = message;
    this.code = code;
  }

  private final String message;
  private final String code;

  @Override
  public String getErrorCode() {
    return code;
  }

  @Override
  public String printErrorMessage() {
    return message;
  }

  /**
   * logs this error
   * @param sourcePosition ast node to infer the location of this error
   * @param args arguments for a {@link String#format(String, Object...)} and {@link Error#printErrorMessage()} call
   */
  public void logAt(ASTNode sourcePosition, Object... args){
    Log.error(String.format(getErrorCode()+": "+printErrorMessage(), args), sourcePosition.get_SourcePositionStart());
  }
}