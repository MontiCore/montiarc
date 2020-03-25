/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import java.util.ArrayList;
import java.util.List;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTBlock;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTTransition;
import montiarc._cocos.MontiArcASTTransitionCoCo;

/**
 * Context condition for checking, if there is not more than one assignment for each variable or
 * port in a reaction of a transition. E.g. Transition S [true] {v = 1} / {x = 1, x = 5} does not
 * have a valid reaction.
 *
 * @implements [RRW14a] S3ts: In every cycle at most one message per port is sent (partially).
 * 
 */
public class MultipleAssignmentsSameIdentifier implements MontiArcASTTransitionCoCo {
  
  @Override
  public void check(ASTTransition node) {
    if (node.isPresentReaction()) {
      ASTBlock reaction = node.getReaction();
      
      List<String> usedNames = new ArrayList<>();
      for (ASTIOAssignment assignment : reaction.getIOAssignmentList()) {
        if (assignment.isPresentName()) {
          if (usedNames.contains(assignment.getName())) {
            // An assignment was already defined
            Log.error(
                "0xMA019 There are multiple I/O-Assignments for port or variable "
                    + assignment.getName() + " in transition " + node.toString() + ".",
                reaction.get_SourcePositionStart());
          }
          else {
            // No assignment for port/var assignment.getName() defined yet.
            usedNames.add(assignment.getName());
          }
        }
      }
    }
  }
}
