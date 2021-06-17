/* (c) https://github.com/MontiCore/monticore */
package arcbehaviorbasis;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;
import montiarc.util.Error;

public enum BehaviorError implements Error {
  MULTIPLE_BEHAVIOR("0xMA2000",
      "The component type %s defines multiple behaviors, but only one is allowed at max"),
  BEHAVIOR_IN_COMPOSED_COMPONENT("0xMA2001",
      "Only atomic components may have behavior specifications, but %s is composed."),
  NO_INITIAL_STATE("0xMA2002", "The automaton of %s lacks an initial state."),
  MANY_INITIAL_STATES("0xMA2003",
      "Automata may not have more than one initial state, but the one of %s has %d: %s and %s"),
  FIELD_IN_STATECHART_MISSING("0xMA2004", "There is no variable or port called '%s' in component '%s'."),
  READ_FROM_OUTGOING_PORT("0xMA2005", "Cannot read from the outgoing port '%s' of component '%s'."),
  WRITE_TO_INCOMING_PORT("0xMA2006", "Cannot write to the incoming port '%s' of component '%s'."),
  WRITE_TO_READONLY_VARIABLE("0xMA2007", "Cannot write to readonly variable '%s' of component '%s'."),
  WRITE_TO_LITERAL("0xMA2008", "Cannot assign variables to a literal."),
  ASSIGN_TO_NOT_NAME("0xMA2010", "Cannot %s %s."),
  UNSUPPORTED_EVENT("0xMA2011", "Events are not supported."),
  REDUNDANT_INITIAL_DECLARATION("0xMA2012", "The state '%s' has multiple initial outputs."),
  INITIAL_STATE_REFERENCE_MISSING("0xMA2013", "The state '%s' referenced here does not exist.");

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