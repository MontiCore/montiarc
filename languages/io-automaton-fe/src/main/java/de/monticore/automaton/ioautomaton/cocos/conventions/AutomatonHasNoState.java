package de.monticore.automaton.ioautomaton.cocos.conventions;

import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContent;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContentCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an IO-Automaton has no states.
 * 
 * @author Gerrit
 */
public class AutomatonHasNoState implements IOAutomatonASTAutomatonContentCoCo {
  
  @Override
  public void check(ASTAutomatonContent node) {
    if (node.getStateDeclarations().isEmpty()) {
      Log.error("0xAA130 The automaton has no states.", node.get_SourcePositionStart());
    }
  }
  
}
