package montiarc.cocos;

import java.util.HashSet;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTState;
import montiarc._ast.ASTStateDeclaration;
import montiarc._ast.ASTTransition;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if every state used in a transition has been defined
 * in an {@link ASTStateDeclaration}.
 * 
 * @implements [Wor16] AR3: Used states exist. (p. 104, Lst. 5.21)
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
        Log.error("0xMA026 The source " + sourceName + " in " + sourceName + "->" + targetName + " was not defined as a state.", transition.get_SourcePositionStart());
      }
      if (!names.contains(targetName)) {
        Log.error("0xMA027 The target " + targetName + " in " + sourceName + "->" + targetName + " was not defined as a state.", transition.get_SourcePositionStart());
      }
    }
  }
  
}
