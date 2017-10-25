package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTVariable;
import montiarc._cocos.MontiArcASTVariableCoCo;

/**
 * Context condition for checking, if all fields of an IO-Automaton start with a
 * lower case letter.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class VariableNameIsLowerCase implements MontiArcASTVariableCoCo {

  @Override
  public void check(ASTVariable var) {
      if (!Character.isLowerCase(var.getName().charAt(0))) {
        Log.warn("0xMA018 The name of variable " + var.getName() + " should start with a lowercase letter.", var.get_SourcePositionStart());
      }
  }
}
