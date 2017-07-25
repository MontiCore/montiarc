package de.monticore.lang.montiarc.cocos;

import de.monticore.lang.montiarc.montiarc._ast.ASTComponentVariable;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTComponentVariableCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if all fields of an IO-Automaton start with a
 * lower case letter.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class ComponentVariableNameIsLowerCase implements MontiArcASTComponentVariableCoCo {

  @Override
  public void check(ASTComponentVariable var) {
      if (!Character.isLowerCase(var.getName().charAt(0))) {
        Log.warn("0xAA160 The name of variable " + var.getName() + " should start with a lowercase letter.", var.get_SourcePositionStart());
      }
  }
}
