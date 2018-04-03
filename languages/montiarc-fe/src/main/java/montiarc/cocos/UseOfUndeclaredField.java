package montiarc.cocos;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTPort;
import montiarc._ast.ASTVariableDeclaration;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

/**
 * Context condition for checking, if a reference is used inside an automaton which has not been
 * defined in an {@link ASTVariableDeclaration} or as {@link ASTPort}.
 *
 * @implements [Wor16] AR1: Names used in guards, valuations, and assignments exist in the
 * automaton. (p. 102, Lst. 5.19)
 * @author Gerrit Leonhardt, Andreas Wortmann
 */
public class UseOfUndeclaredField implements MontiArcASTIOAssignmentCoCo {
  
  @Override
  public void check(ASTIOAssignment node) {
    // only check left side of IOAssignment, right side is implicitly checked
    // when resolving type of the valuations
    
    if (node.isPresentName()) {
      String name = node.getName();
      Scope scope = node.getEnclosingScope().get();
      boolean foundVar = scope.resolve(name, VariableSymbol.KIND).isPresent();
      boolean foundPort = scope.resolve(name, PortSymbol.KIND).isPresent();
      if (!foundVar && !foundPort) {
        Log.error("0xMA079: The name '" + name + "' is used in assignment, but is neither declared "
            + "a port, nor as a variable.",
            node.get_SourcePositionStart());
      }
    }
  }
  
}
