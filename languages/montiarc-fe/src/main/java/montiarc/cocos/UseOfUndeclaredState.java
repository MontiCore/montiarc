/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if every state used in a transition has
 * been defined in an {@link ASTStateDeclaration}.
 * 
 * @implements [Wor16] AR3: Used states exist. (p. 104, Lst. 5.21)
 * @author Andreas Wortmann
 */
public class UseOfUndeclaredState implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    // Collect defined States
    HashSet<String> stateNames = new HashSet<>();
    for (ASTStateDeclaration stateDecl : node.getStateDeclarationList()) {
      for (ASTState state : stateDecl.getStateList()) {
        stateNames.add(state.getName());
      }
    }

    // Check initial state declarations
    // Collect all InitialStateNames
    List<String> initialStateNames = new ArrayList<String>();
    for (ASTInitialStateDeclaration astInitialStateDecl : node.getInitialStateDeclarationList()) {
      initialStateNames.addAll(astInitialStateDecl.getNameList());
    }

    // Search each Initial State Name in the List of all States - abort if
    // you can't find one
    for (String initialName : initialStateNames) {
      if (!stateNames.contains(initialName)) {
        Log.error(
            String.format("0xMA025 State %s is labeled as initial but " +
                              "is not definied as state.", initialName),
            node.get_SourcePositionStart());
      }
    }

    // Check transitions
    for (ASTTransition transition : node.getTransitionList()) {
      String sourceName = transition.getSource();
      String targetName = transition.getTargetOpt().orElse(sourceName);
      
      if (!stateNames.contains(sourceName)) {
        Log.error(String.format("0xMA026 The source %s in %s->%s was " +
                                    "not defined as a state.",
            sourceName, sourceName, targetName),
            transition.get_SourcePositionStart());
      }
      if (!stateNames.contains(targetName)) {
        Log.error(String.format("0xMA027 The target %s in %s->%s was " +
                                    "not defined as a state.",
            targetName, sourceName, targetName),
            transition.get_SourcePositionStart());
      }
    }
  }
  
}
