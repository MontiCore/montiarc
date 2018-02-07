package montiarc.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._cocos.MontiArcASTIOAssignmentCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc._ast.ASTIOAssignment;

import java.util.Optional;
import montiarc._ast.ASTPort;
import montiarc._ast.ASTVariableDeclaration;
import de.monticore.symboltable.Scope;

/**
 * Context condition for checking, if a reference is used inside an automaton
 * which has not been defined in an {@link ASTVariableDeclaration} or as {@link ASTPort}.
 *
 * @implements [Wor16] AR1: Names used in guards, valuations, and assignments exist in the automaton. (p. 102, Lst. 5.19)
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
      String refKind = foundVar ? "Variable" : "Port";
      if (!foundVar) {
    	  if(!foundPort) {
    		  Log.error("0xMA079: "+refKind+" " + node.getName().get() + " is used as a field, but is not declared as such.", node.get_SourcePositionStart());
    	      
    	  }
        }
    
      
    }
  }
  
}
