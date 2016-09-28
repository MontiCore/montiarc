package de.monticore.automaton.ioautomaton.cocos.conventions;

import de.monticore.automaton.ioautomaton._ast.ASTState;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTStateCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if all states of an IO-Automaton start with
 * an Uppercase letter.
 * 
 * @author Gerrit Leonhardt
 */
public class StateUppercase implements IOAutomatonASTStateCoCo {
  
  @Override
  public void check(ASTState node) {
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error("0xAA190 The name of state " + node.getName() + " should start with an uppercase letter.", node.get_SourcePositionStart());
    }
  }
  
}
