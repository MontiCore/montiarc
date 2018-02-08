package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTState;
import montiarc._cocos.MontiArcASTStateCoCo;

/**
 * Context condition for checking, if all states of an IO-Automaton start with
 * an Uppercase letter.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonStateUppercase implements MontiArcASTStateCoCo {
  
  @Override
  public void check(ASTState node) {
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error("0xMA021 The name of state " + node.getName() + " should start with an uppercase letter.", node.get_SourcePositionStart());
    }
  }
  
}
