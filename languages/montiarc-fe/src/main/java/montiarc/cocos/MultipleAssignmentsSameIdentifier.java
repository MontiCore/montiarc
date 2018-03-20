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
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class MultipleAssignmentsSameIdentifier implements MontiArcASTTransitionCoCo {
  
  @Override
  public void check(ASTTransition node) {
    if (node.getReaction().isPresent()) {
      ASTBlock reaction = node.getReaction().get();
      
      List<String> usedNames = new ArrayList<>();
      for (ASTIOAssignment assignment : reaction.getIOAssignments()) {
        if (assignment.getName().isPresent()) {
          if (usedNames.contains(assignment.getName().get())) {
            // An assignment was already defined
            Log.error(
                "0xMA019 There are multiple I/O-Assignments for port or variable "
                    + assignment.getName().get() + " in transition " + node.toString() + ".",
                reaction.get_SourcePositionStart());
          }
          else {
            // No assignment for port/var assignment.getName() defined yet.
            usedNames.add(assignment.getName().get());
          }
        }
      }
    }
  }
}
