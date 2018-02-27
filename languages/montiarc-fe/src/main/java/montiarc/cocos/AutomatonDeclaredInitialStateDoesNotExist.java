package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._ast.ASTState;
import montiarc._ast.ASTStateDeclaration;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if all states, labeled as initial states have been defined as
 * states in a {@link ASTStateDeclaration}.
 *
 * @author Andreas Wortmann
 */
public class AutomatonDeclaredInitialStateDoesNotExist implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
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
        Log.error(
            "0xMA025 State " + initialName + " is labeled as initial but is not definied as state.",
            node.get_SourcePositionStart());
      }
    }
  }
  
}
