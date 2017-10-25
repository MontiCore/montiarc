package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if an IO-Automaton has no initial
 * states.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonHasNoInitialState implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    boolean hasStates = !node.getStateDeclarations().isEmpty();
    boolean hasNoInitialStates = node.getInitialStateDeclarations().isEmpty();
    if (hasStates && hasNoInitialStates) {
      Log.error("0xMA013 The automaton has no initial states.", node.get_SourcePositionStart());
    }
  }
  
}
