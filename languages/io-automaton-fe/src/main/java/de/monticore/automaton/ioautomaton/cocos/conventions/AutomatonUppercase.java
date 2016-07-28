package de.monticore.automaton.ioautomaton.cocos.conventions;

import de.monticore.automaton.ioautomaton._ast.ASTAutomaton;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if the name of an IO-Automaton starts with an
 * uppercase letter.
 * 
 * @author Gerrit
 */
public class AutomatonUppercase implements IOAutomatonASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error("0xAA140 The name of the automaton should start with an uppercase letter.", node.get_SourcePositionStart());
    }
  }
  
}
