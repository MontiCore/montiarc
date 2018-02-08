package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if an IO-Automaton has no states.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonHasNoState implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    if (node.getStateDeclarations().isEmpty()) {
      Log.error("0xMA014 The automaton has no states.", node.get_SourcePositionStart());
    }
  }
  
}
