package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._ast.ASTTransition;
import montiarc._cocos.MontiArcASTInitialStateDeclarationCoCo;
import montiarc._cocos.MontiArcASTTransitionCoCo;

/**
 * Context condition for checking, if a reaction of a transition contains an
 * alternative. This would be something like A->B [...] {...} / {b = alt{1,2,3}}
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class ReactionWithAlternatives implements MontiArcASTInitialStateDeclarationCoCo, MontiArcASTTransitionCoCo {
  
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
