package montiarc.cocos;

import java.util.Optional;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTInitialStateDeclaration;
import montiarc._ast.ASTValuation;
import montiarc._cocos.MontiArcASTInitialStateDeclarationCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Context condition for checking, if the name of the assignments in the
 * reaction of an initial state declaration fit to their values in terms of
 * types. output int a; initial A / {a = 5} would be allowed initial A / {a =
 * true} would be not.
 * 
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since $Version$
 */
public class AutomatonInitialReactionTypeDoesNotFitOutputType implements MontiArcASTInitialStateDeclarationCoCo {
  
  @Override
  public void check(ASTInitialStateDeclaration node) {
    if (node.getBlock().isPresent()) {
      for (ASTIOAssignment assign : node.getBlock().get().getIOAssignments()) {
        if (assign.nameIsPresent()) {
          String currentNameToResolve = assign.getName().get();
          
          Optional<VariableSymbol> varSymbol = node.getEnclosingScope().get().resolve(currentNameToResolve, VariableSymbol.KIND);
          Optional<PortSymbol> portSymbol = node.getEnclosingScope().get().resolve(currentNameToResolve, PortSymbol.KIND);
          if (portSymbol.isPresent()) {
            if (portSymbol.get().isIncoming()) {
              Log.error("0xMA038 Did not find matching Variable or Output with name " + currentNameToResolve, assign.get_SourcePositionStart());
            }
            else {
              checkAssignment(assign, portSymbol.get().getTypeReference(), currentNameToResolve);
            }
          }
          else if(varSymbol.isPresent()) {
            checkAssignment(assign, varSymbol.get().getTypeReference(), currentNameToResolve);
          } else {
            Log.error("0xMA038 Did not find matching Variable or Output with name " + currentNameToResolve, assign.get_SourcePositionStart());
          }
        }
      }
    }
  }
  
  private void checkAssignment(ASTIOAssignment assign, JTypeReference<? extends JTypeSymbol> typeRef, String currentNameToResolve) {
    if (assign.valueListIsPresent()) {
      JTypeReference<? extends JTypeSymbol> varType = typeRef;
      for (ASTValuation val : assign.getValueList().get().getAllValuations()) {
        Optional<? extends JavaTypeSymbolReference> exprType = TypeCompatibilityChecker.getExpressionType(val.getExpression());
        if (!exprType.isPresent()) {
          Log.error("0xMA040 Could not resolve type of expression for checking the initial reaction.", assign.get_SourcePositionStart());
        }
        else if (!TypeCompatibilityChecker.doTypesMatch(exprType.get(), varType)) {
          Log.error("0xMA039 Type of Variable/Output " + currentNameToResolve + " in the initial state declaration does not match the type of its assigned expression. Type " +
                exprType.get().getName() + " can not cast to type " + varType.getName() + ".", val.get_SourcePositionStart());
        }
      }
    }
  }
  
}
