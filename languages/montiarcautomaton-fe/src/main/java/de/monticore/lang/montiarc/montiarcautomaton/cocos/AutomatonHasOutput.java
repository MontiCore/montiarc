package de.monticore.lang.montiarc.montiarcautomaton.cocos;

import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContext;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContextCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an IO-Automaton contains output fields.
 * 
 * @author Gerrit
 */
public class AutomatonHasOutput implements IOAutomatonASTAutomatonContextCoCo {
  
  @Override
  public void check(ASTAutomatonContext node) {
    if (!node.getOutputDeclarations().isEmpty()) {
      Log.warn("0xAB120 The automaton contains outputs.", node.get_SourcePositionStart());
    }
  }
  
}
