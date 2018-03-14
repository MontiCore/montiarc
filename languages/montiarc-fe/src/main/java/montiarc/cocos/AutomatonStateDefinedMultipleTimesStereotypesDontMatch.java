package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTState;
import montiarc._ast.ASTStateDeclaration;
import montiarc._ast.ASTStereoValue;
import montiarc._ast.ASTStereotype;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if states have been defined multiple times with different
 * stereotypes.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonStateDefinedMultipleTimesStereotypesDontMatch
    implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) { // Collect all states
    List<ASTState> allStates = new ArrayList<ASTState>();
    for (ASTStateDeclaration stateDecl : node.getStateDeclarationList()) {
      allStates.addAll(stateDecl.getStateList());
    }
    
    // Check for each state, if in the following states there is one with the
    // same name
    for (int i = 0; i < allStates.size(); i++) {
      ASTState current = allStates.get(i);
      for (int j = i + 1; j < allStates.size(); j++) {
        if (allStates.get(j).getName().equals(current.getName())) {
          ASTState checkMe = allStates.get(j);
          // Check if the stereotypes match
          ASTStereotype currentStereotype = current.getStereotypeOpt().orElse(null);
          ASTStereotype checkMeStereotype = checkMe.getStereotypeOpt().orElse(null);
          if (currentStereotype != null && checkMeStereotype != null
              && currentStereotype.getValuesList().size() == checkMeStereotype.getValuesList()
                  .size()) {
            for (ASTStereoValue valueCurrent : currentStereotype.getValuesList()) {
              boolean found = false;
              for (ASTStereoValue valueToCheck : checkMeStereotype.getValuesList()) {
                if (valueCurrent.getName().equals(valueToCheck.getName())) {
                  found = true;
                  break;
                }
              }
              if (!found) {
                Log.error(
                    "0xMA033 The state " + current.getName()
                        + " is defined more than once with different stereotypes.",
                    current.get_SourcePositionStart());
              }
            }
            
          }
          else {
            // This means one of both/both are null or one has more stereotypes
            // than another
            if ((currentStereotype != null && checkMeStereotype == null)
                || (currentStereotype == null && checkMeStereotype != null)) {
              Log.error(
                  "0xMA034 The state " + current.getName()
                      + " is defined more than once with different stereotypes.",
                  current.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }
  
}
