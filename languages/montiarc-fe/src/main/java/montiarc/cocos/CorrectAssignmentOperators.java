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
    for (ASTTransition transition : node.getTransitionList()) {
      if (transition.isPresentStimulus()) {
        for (ASTIOAssignment stimulus : transition.getStimulus().getIOAssignmentList()) {
          if (stimulus.getOperator() == ASTConstantsMontiArc.SINGLE) { // == expected
            Log.error("0xMA016 The stimulus '" + stimulus + "' uses the wrong assignment operator.",
                stimulus.get_SourcePositionStart());
          }
        }
      }
      if (transition.isPresentReaction()) {
        for (ASTIOAssignment reaction : transition.getReaction().getIOAssignmentList()) {
          if (reaction.getOperator() == ASTConstantsMontiArc.DOUBLE) { // = expected
            Log.error("0xMA017 The reaction '" + reaction + "' uses the wrong assignment operator.",
                reaction.get_SourcePositionStart());
          }
        }
      }
    }
  }
}
