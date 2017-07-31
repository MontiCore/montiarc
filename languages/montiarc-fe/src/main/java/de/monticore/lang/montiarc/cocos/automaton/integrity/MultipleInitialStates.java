package de.monticore.lang.montiarc.cocos.automaton.integrity;

import de.monticore.lang.montiarc.montiarc._ast.ASTInitialStateDeclaration;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTInitialStateDeclarationCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition that forbids the usage of multiple initial states.
 *
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class MultipleInitialStates implements MontiArcASTInitialStateDeclarationCoCo {

  @Override
  public void check(ASTInitialStateDeclaration node) {
    if (node.getNames().size() > 1) {
      Log.error("0xAC110 Multiple initial states are not supported.", node.get_SourcePositionStart());
    }
  }
}
