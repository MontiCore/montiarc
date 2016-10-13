package de.monticore.automaton.ioautomaton.cocos.conventions;

import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContext;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContextCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an IO-Automaton has no output fields.
 * 
 * @author Gerrit Leonhardt
 */
public class AutomatonHasNoOutput implements IOAutomatonASTAutomatonContextCoCo {
  
  @Override
  public void check(ASTAutomatonContext node) {
    if (node.getOutputDeclarations().isEmpty()) {
      Log.warn("0xAA120 The automaton has no outputs.", node.get_SourcePositionStart());
    }
  }
  
}
