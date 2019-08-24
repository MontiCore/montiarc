/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if an IO-Automaton has no states.
 * 
 * @implements [Wor16] AC3: The automaton has at least one state. (p. 99, Lst. 5.13)
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonHasNoState implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    if (node.getStateDeclarationList().isEmpty()) {
      Log.error("0xMA014 The automaton has no states.", node.get_SourcePositionStart());
    }
  }
  
}
