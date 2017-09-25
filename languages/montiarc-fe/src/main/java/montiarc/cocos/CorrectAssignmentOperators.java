package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomaton;
import montiarc._ast.ASTConstantsMontiArc;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTTransition;
import montiarc._cocos.MontiArcASTAutomatonCoCo;

/**
 * Context condition for checking, if a correct assignment operation is used.
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class CorrectAssignmentOperators implements MontiArcASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    for (ASTTransition transition : node.getTransitions()) {
      if (transition.getStimulus().isPresent()) {
        for (ASTIOAssignment stimulus : transition.getStimulus().get().getIOAssignments()) {
          if (stimulus.getOperator() == ASTConstantsMontiArc.SINGLE) { // == expected
            Log.error("0xAA150 The stimulus '" + stimulus + "' uses the wrong assignment operator.", stimulus.get_SourcePositionStart());
          }
        }
      }
      if (transition.getReaction().isPresent()) {
        for (ASTIOAssignment reaction : transition.getReaction().get().getIOAssignments()) {
          if (reaction.getOperator() == ASTConstantsMontiArc.DOUBLE) { // = expected
            Log.error("0xAA151 The reaction '" + reaction + "' uses the wrong assignment operator.", reaction.get_SourcePositionStart());
          }
        }
      }
    }
  }
}
