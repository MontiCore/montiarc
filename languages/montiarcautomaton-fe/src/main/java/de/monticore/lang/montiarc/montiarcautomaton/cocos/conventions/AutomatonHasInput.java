package de.monticore.lang.montiarc.montiarcautomaton.cocos.conventions;

import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContext;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContextCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an IO-Automaton contains input fields.
 * 
 * @author Gerrit Leonhardt
 */
public class AutomatonHasInput implements IOAutomatonASTAutomatonContextCoCo {
  
  @Override
  public void check(ASTAutomatonContext node) {
    if (!node.getInputDeclarations().isEmpty()) {
      Log.error("0xAB110 The automaton contains inputs.", node.get_SourcePositionStart());
    }
  }
  
}
