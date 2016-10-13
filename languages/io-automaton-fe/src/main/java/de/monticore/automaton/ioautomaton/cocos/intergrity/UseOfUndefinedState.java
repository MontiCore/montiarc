package de.monticore.automaton.ioautomaton.cocos.intergrity;

import java.util.HashSet;
import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContent;
import de.monticore.automaton.ioautomaton._ast.ASTState;
import de.monticore.automaton.ioautomaton._ast.ASTStateDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTTransition;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContentCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if every state used in a transition has been defined
 * in an {@link ASTStateDeclaration}.
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   $Version$
 *
 */
public class UseOfUndefinedState implements IOAutomatonASTAutomatonContentCoCo {

  @Override
  public void check(ASTAutomatonContent node) {
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
