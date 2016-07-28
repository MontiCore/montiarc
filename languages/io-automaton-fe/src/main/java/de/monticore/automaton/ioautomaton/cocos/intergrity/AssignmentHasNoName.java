package de.monticore.automaton.ioautomaton.cocos.intergrity;

import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTIOAssignmentCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if an IOAssignment has no name. This can only
 * be the case, if the {@link IOSymTabCreator} could not calculate a unique
 * match for a IOAssignment.
 * 
 * @author Gerrit
 */
public class AssignmentHasNoName implements IOAutomatonASTIOAssignmentCoCo{  

  @Override
  public void check(ASTIOAssignment node) {
    if (!node.nameIsPresent()) {
      Log.error("0xAA200 Could not find a unique matching type for the assignment '" + node + "'.", node.get_SourcePositionStart());
    }    
  }
  
}
