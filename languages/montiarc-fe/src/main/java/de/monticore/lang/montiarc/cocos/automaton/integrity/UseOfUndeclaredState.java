package de.monticore.lang.montiarc.cocos.automaton.integrity;

import java.util.HashSet;

import de.monticore.lang.montiarc.montiarc._ast.ASTAutomaton;
import de.monticore.lang.montiarc.montiarc._ast.ASTState;
import de.monticore.lang.montiarc.montiarc._ast.ASTStateDeclaration;
import de.monticore.lang.montiarc.montiarc._ast.ASTTransition;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTAutomatonCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if every state used in a transition has been defined
 * in an {@link ASTStateDeclaration}.
 *
 * @author Andreas Wortmann
 */
public class UseOfUndeclaredState implements MontiArcASTAutomatonCoCo {

  @Override
  public void check(ASTAutomaton node) {
    // Collect defined States
    HashSet<String> names = new HashSet<>();
    for (ASTStateDeclaration stateDecl : node.getStateDeclarations()) {
      for (ASTState state : stateDecl.getStates()) {
        names.add(state.getName());
      }
    }
    
    // Check transitions
    for (ASTTransition transition : node.getTransitions()) {
      String sourceName = transition.getSource();
      String targetName = transition.getTarget().orElse(sourceName);
      
      if (!names.contains(sourceName)) {
        Log.error("0xAA220 The source " + sourceName + " in " + sourceName + "->" + targetName + " was not defined as a state.", transition.get_SourcePositionStart());
      }
      if (!names.contains(targetName)) {
        Log.error("0xAA221 The target " + targetName + " in " + sourceName + "->" + targetName + " was not defined as a state.", transition.get_SourcePositionStart());
      }
    }
  }
  
}
