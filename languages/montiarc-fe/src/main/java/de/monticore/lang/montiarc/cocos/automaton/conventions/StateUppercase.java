package de.monticore.lang.montiarc.cocos.automaton.conventions;

import de.monticore.lang.montiarc.montiarc._ast.ASTState;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTStateCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if all states of an IO-Automaton start with
 * an Uppercase letter.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class StateUppercase implements MontiArcASTStateCoCo {
  
  @Override
  public void check(ASTState node) {
    if (!Character.isUpperCase(node.getName().charAt(0))) {
      Log.error("0xAA190 The name of state " + node.getName() + " should start with an uppercase letter.", node.get_SourcePositionStart());
    }
  }
  
}
