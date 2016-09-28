package de.monticore.automaton.ioautomaton.cocos.conventions;

import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContent;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContentCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an IO-Automaton has no initial
 * states.
 * 
 * @author Gerrit Leonhardt
 */
public class AutomatonHasNoInitialState implements IOAutomatonASTAutomatonContentCoCo {
  
  @Override
  public void check(ASTAutomatonContent node) {
    boolean hasStates = !node.getStateDeclarations().isEmpty();
    boolean hasNoInitialStates = node.getInitialStateDeclarations().isEmpty();
    if (hasStates && hasNoInitialStates) {
      Log.error("0xAA100 The automaton has no initial states.", node.get_SourcePositionStart());
    }
  }
  
}
