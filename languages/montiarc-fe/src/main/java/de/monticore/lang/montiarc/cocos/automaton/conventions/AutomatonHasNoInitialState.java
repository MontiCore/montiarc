package de.monticore.lang.montiarc.cocos.automaton.conventions;

import de.monticore.lang.montiarc.montiarc._ast.ASTAutomaton;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTAutomatonCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an IO-Automaton has no initial
 * states.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonHasNoInitialState implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    boolean hasStates = !node.getStateDeclarations().isEmpty();
    boolean hasNoInitialStates = node.getInitialStateDeclarations().isEmpty();
    if (hasStates && hasNoInitialStates) {
      Log.error("0xAA100 The automaton has no initial states.", node.get_SourcePositionStart());
    }
  }
  
}
