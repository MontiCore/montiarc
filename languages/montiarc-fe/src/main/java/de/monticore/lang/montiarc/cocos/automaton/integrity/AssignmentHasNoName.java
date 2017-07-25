package de.monticore.lang.montiarc.cocos.automaton.integrity;

import de.monticore.lang.montiarc.montiarc._ast.ASTIOAssignment;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTIOAssignmentCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an IOAssignment has no name. This can only
 * be the case, if the {@link IOSymTabCreator} could not calculate a unique
 * match for a IOAssignment.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AssignmentHasNoName implements MontiArcASTIOAssignmentCoCo {  

  @Override
  public void check(ASTIOAssignment node) {
    if (!node.nameIsPresent()) {
      Log.error("0xAA200 Could not find a unique matching type for the assignment '" + node + "'.", node.get_SourcePositionStart());
    }    
  }
  
}
