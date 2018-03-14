package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._ast.ASTTransition;
import montiarc._cocos.MontiArcASTInitialStateDeclarationCoCo;
import montiarc._cocos.MontiArcASTTransitionCoCo;

/**
 * Context condition for checking, if a reaction of a transition contains an alternative. This would
 * be something like A->B [...] {...} / {b = alt{1,2,3}}
 * 
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class AutomatonReactionWithAlternatives
    implements MontiArcASTInitialStateDeclarationCoCo, MontiArcASTTransitionCoCo {
  
  @Override
  public void check(ASTTransition node) {
    if (node.isReactionPresent()) {
      for (ASTIOAssignment assign : node.getReaction().getIOAssignmentList()) {
        if (assign.isAlternativePresent()) {
          Log.error("0xMA020 There are alternative values in a reaction.",
              assign.getAlternative().get_SourcePositionStart());
        }
      }
    }
  }
  
  @Override
  public void check(ASTInitialStateDeclaration node) {
    if (node.isBlockPresent()) {
      for (ASTIOAssignment assign : node.getBlock().getIOAssignmentList()) {
        if (assign.isAlternativePresent()) {
          Log.error("0xMA020 There are alternative values in a reaction.",
              assign.getAlternative().get_SourcePositionStart());
        }
      }
    }
  }
}
