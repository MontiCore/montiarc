package de.monticore.lang.montiarc.montiarcautomaton.cocos;

import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContext;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContextCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an IO-Automaton contains variable fields.
 * 
 * @author Gerrit
 */
public class AutomatonHasVariable implements IOAutomatonASTAutomatonContextCoCo {
  
  @Override
  public void check(ASTAutomatonContext node) {
    if (!node.getVariableDeclarations().isEmpty()) {
      Log.warn("0xAB130 The automaton contains variables.", node.get_SourcePositionStart());
    }
  }
  
}
