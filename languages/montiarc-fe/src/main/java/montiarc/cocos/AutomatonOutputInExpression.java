package montiarc.cocos;

import java.util.Optional;

import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTGuardExpression;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTTransition;
import montiarc._cocos.MontiArcASTTransitionCoCo;
import montiarc._symboltable.PortSymbol;

/**
 * Checks whether output ports are used within a expression in a automaton transition.
 * 
 * @implements [Wor16] AR2: Inputs, outputs, and variables are used correctly. (p. 103, Lst. 520)
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class AutomatonOutputInExpression implements MontiArcASTTransitionCoCo {
  
  @Override
  public void check(ASTTransition node) {
    if (node.isGuardPresent()) {
      Scope scope = node.getEnclosingScope().get();
      ASTExpression exp = node.getGuard().getGuardExpression().getExpression();
//      TODO@AB
//      if(expr instancof A)
//      for (ASTIOAssignment ioa : ) {
//        if (ioa.isPresentName()) {
//          Optional<PortSymbol> found = scope.resolve(ioa.getName(), PortSymbol.KIND);
//          if (found.isPresent() && found.get().isOutgoing()) {
//            Log.error(
//                "0xMA022 Field " + found.get().getName()
//                    + " is an Ouput and not allowed in Expressions.",
//                node.get_SourcePositionStart());
//          }
//        }
//      }
    }
    
  }
}
