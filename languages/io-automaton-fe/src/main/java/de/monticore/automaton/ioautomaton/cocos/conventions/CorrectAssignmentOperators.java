package de.monticore.automaton.ioautomaton.cocos.conventions;

import de.monticore.automaton.ioautomaton._ast.ASTAutomatonContent;
import de.monticore.automaton.ioautomaton._ast.ASTConstantsIOAutomaton;
import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._ast.ASTTransition;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonContentCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if a correct assignment operation is used.
 * 
 * @author Gerrit
 */
public class CorrectAssignmentOperators implements IOAutomatonASTAutomatonContentCoCo {
  
  @Override
  public void check(ASTAutomatonContent node) {
    for (ASTTransition transition : node.getTransitions()) {
      if (transition.getStimulus().isPresent()) {
        for (ASTIOAssignment stimulus : transition.getStimulus().get().getIOAssignments()) {
          if (stimulus.getOperator() == ASTConstantsIOAutomaton.SINGLE) { // == expected
            Log.error("0xAA150 The stimulus '" + stimulus + "' uses the wrong assignment operator.", stimulus.get_SourcePositionStart());
          }
        }
      }
      if (transition.getReaction().isPresent()) {
        for (ASTIOAssignment reaction : transition.getReaction().get().getIOAssignments()) {
          if (reaction.getOperator() == ASTConstantsIOAutomaton.DOUBLE) { // = expected
            Log.error("0xAA151 The reaction '" + reaction + "' uses the wrong assignment operator.", reaction.get_SourcePositionStart());
          }
        }
      }
    }
  }
}
