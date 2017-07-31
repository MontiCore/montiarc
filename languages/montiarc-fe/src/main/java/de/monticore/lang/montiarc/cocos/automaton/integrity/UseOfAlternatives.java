package de.monticore.lang.montiarc.cocos.automaton.integrity;

import de.monticore.lang.montiarc.montiarc._ast.ASTAlternative;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTAlternativeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition that forbids the usage of multiple alternatives.
 *
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class UseOfAlternatives implements MontiArcASTAlternativeCoCo {
  
  @Override
  public void check(ASTAlternative node) {
    if (node.getValueLists().size() > 1) {
      Log.error("0xAC100 Multiple alternatives are not supported.", node.get_SourcePositionStart());
    }
  }
}
