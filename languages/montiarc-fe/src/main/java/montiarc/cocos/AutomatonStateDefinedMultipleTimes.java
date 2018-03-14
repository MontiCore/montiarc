package montiarc.cocos;

import java.util.HashSet;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTState;
import montiarc._ast.ASTStateDeclaration;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if a state is defined multiple times with the same stereotypes.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonStateDefinedMultipleTimes implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    HashSet<String> names = new HashSet<>();
    for (ASTStateDeclaration decl : node.getStateDeclarationList()) {
      for (ASTState state : decl.getStateList()) {
        if (names.contains(state.getName())) {
          Log.error("0xMA031 State " + state.getName() + " is defined more than once.",
              state.get_SourcePositionStart());
        }
        else {
          names.add(state.getName());
        }
      }
    }
  }
  
}
