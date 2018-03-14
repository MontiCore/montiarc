package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if an IO-Automaton has no initial states.
 * 
 * @implements [Wor16] AC4: The automaton has at least one initial state. (p. 99, Lst. 5.14)
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonHasNoInitialState implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    boolean hasStates = !node.getStateDeclarationList().isEmpty();
    boolean hasNoInitialStates = node.getInitialStateDeclarationList().isEmpty();
    if (hasStates && hasNoInitialStates) {
      Log.error("0xMA013 The automaton has no initial states.", node.get_SourcePositionStart());
    }
  }
  
}
