package de.monticore.lang.montiarc.cocos.automaton.conventions;

import de.monticore.lang.montiarc.montiarc._ast.ASTAutomaton;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTAutomatonCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an IO-Automaton has no states.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonHasNoState implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    if (node.getStateDeclarations().isEmpty()) {
      Log.error("0xAA130 The automaton has no states.", node.get_SourcePositionStart());
    }
  }
  
}
