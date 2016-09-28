package de.monticore.automaton.ioautomaton.cocos.conventions;

import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._ast.ASTInitialStateDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTTransition;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTInitialStateDeclarationCoCo;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTTransitionCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if a reaction of a transition contains an
 * alternative. This would be something like A->B [...] {...} / {b = alt{1,2,3}}
 * 
 * @author Gerrit Leonhardt
 */
public class ReactionWithAlternatives implements IOAutomatonASTInitialStateDeclarationCoCo, IOAutomatonASTTransitionCoCo {
  
  @Override
  public void check(ASTTransition node) {
    if (node.reactionIsPresent()) {
      for (ASTIOAssignment assign : node.getReaction().get().getIOAssignments()) {
        if (assign.alternativeIsPresent()) {
          Log.error("0xAA180 There are alternative values in a reaction.", assign.getAlternative().get().get_SourcePositionStart());
        }
      }
    }
  }
  
  @Override
  public void check(ASTInitialStateDeclaration node) {
    if (node.blockIsPresent()) {
      for (ASTIOAssignment assign : node.getBlock().get().getIOAssignments()) {
        if (assign.alternativeIsPresent()) {
          Log.error("0xAA181 There are alternative values in a reaction.", assign.getAlternative().get().get_SourcePositionStart());
        }
      }
    }
  }
}
