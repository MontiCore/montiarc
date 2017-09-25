package montiarc.cocos;

import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTIOAssignment;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;

/**
 * Context condition for checking, if a variable is used inside an automaton
 * which has not been defined in an {@link ASTInputDeclaration}, {@link ASTVariableDeclaration} or {@link ASTOutputDeclaration}.
 *
 * @author Gerrit Leonhardt, Andreas Wortmann
 *
 */
public class UseOfUndeclaredField implements MontiArcASTIOAssignmentCoCo {
  
  @Override
  public void check(ASTIOAssignment node) {
    // only check left side of IOAssignment, right side is implicitly checked
    // when resolving type of the valuations
    if (node.nameIsPresent()) {
      Scope scope = node.getEnclosingScope().get();
      boolean isVariable = scope.resolve(node.getName().get(), VariableSymbol.KIND).isPresent();
      boolean isPort = scope.resolve(node.getName().get(), PortSymbol.KIND).isPresent();
      if (!isVariable && !isPort) {
        Log.error("0xAA230 " + node.getName().get() + " is used as a field, but is not declared as such.", node.get_SourcePositionStart());
      }
    }
  }
  
}
