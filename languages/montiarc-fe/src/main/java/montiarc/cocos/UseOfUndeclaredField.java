package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc._ast.ASTIOAssignment;

import java.util.Optional;
import montiarc._ast.ASTPort;
import de.monticore.symboltable.Scope;

/**
 * Context condition for checking, if a variable is used inside an automaton
 * which has not been defined in an {@link ASTInputDeclaration}, {@link ASTVariableDeclaration} or {@link ASTOutputDeclaration}.
 *
 * @author Gerrit Leonhardt
 *
 */
public class UseOfUndeclaredField implements MontiArcASTIOAssignmentCoCo {
  
  @Override
  public void check(ASTIOAssignment node) {
    // only check left side of IOAssignment, right side is implicitly checked
    // when resolving type of the valuations
    if (node.nameIsPresent()) {
    	
      Scope scope = node.getEnclosingScope().get();
      boolean foundVar = scope.resolve(node.getName().get(), VariableSymbol.KIND).isPresent();
      boolean foundPort = scope.resolve(node.getName().get(), PortSymbol.KIND).isPresent();
      if (!foundVar) {
    	  if(!foundPort) {
    		  Log.error("0xMA023: variable " + node.getName().get() + " is used as a field, but is not declared as such.", node.get_SourcePositionStart());
    	      
    	  }
        }
    
      
    }
  }
  
}
