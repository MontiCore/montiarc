package de.monticore.automaton.ioautomaton.cocos.conventions;

import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContext;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContextCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an IO-Automaton has no input
 * 
 * @author Gerrit Leonhardt
 */
public class AutomatonHasNoInput implements IOAutomatonASTAutomatonContextCoCo {
  
  @Override
  public void check(ASTAutomatonContext node) {
    if (node.getInputDeclarations().isEmpty()) {
      Log.warn("0xAA110 The automaton has no inputs.", node.get_SourcePositionStart());
    }
  }
  
}
