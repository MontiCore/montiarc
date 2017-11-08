package de.monticore.automaton.ioautomaton.cocos.intergrity;

import java.util.ArrayList;
import java.util.List;

import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContent;
import de.monticore.automaton.ioautomaton._ast.ASTInitialStateDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTState;
import de.monticore.automaton.ioautomaton._ast.ASTStateDeclaration;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContentCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if all states, labeled as initial states
 * have been defined as states in a {@link ASTStateDeclaration}.
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   $Version$
 *
 */
public class DeclaredInitialStateDoesNotExist implements IOAutomatonASTAutomatonContentCoCo {
  
  @Override
  public void check(ASTAutomatonContent node) {
    // Collect all InitialStateNames
    List<String> initialNames = new ArrayList<String>();
    for (ASTInitialStateDeclaration astInitialStateDecl : node.getInitialStateDeclarations()) {
      initialNames.addAll(astInitialStateDecl.getNames());
    }
    
    // Collect all Statenames
    List<String> stateNames = new ArrayList<String>();
    for (ASTStateDeclaration astStateDecl : node.getStateDeclarations()) {
      for (ASTState state : astStateDecl.getStates()) {
        stateNames.add(state.getName());
      }
    }
    
    // Search each Initial State Name in the List of all States - abort if you
    // can't find one
    for (String initialName : initialNames) {
      if (!stateNames.contains(initialName)) {
        Log.error("0xAA210 State " + initialName + " is labeled as initial but is not definied as state.", node.get_SourcePositionStart());      
      }
    }
  }
  
}
