package montiarc.cocos;

import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTIOAssignment;
import montiarc._ast.ASTTransition;
import montiarc._ast.ASTValuation;
import montiarc._cocos.MontiArcASTTransitionCoCo;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.TransitionSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc.helper.TypeCompatibilityChecker;

/**
 * Context condition for checking, if all assignments inside a reaction of a
 * transition are type-correct.
 *
 * @author Andreas Wortmann
 */
public class AutomatonReactionTypeDoesNotFitOutputType implements MontiArcASTTransitionCoCo {
  
  @Override
  public void check(ASTTransition node) {
    if (node.reactionIsPresent()) {
      for (ASTIOAssignment assign : node.getReaction().get().getIOAssignments()) {
        if (assign.nameIsPresent()) {
          String currentNameToResolve = assign.getName().get();
          
          Scope transitionScope = ((TransitionSymbol) node.getSymbol().get()).getSpannedScope();
          Optional<VariableSymbol> vSymbol = transitionScope.resolve(currentNameToResolve,
              VariableSymbol.KIND);
          Optional<PortSymbol> pSymbol = transitionScope.resolve(currentNameToResolve,
              PortSymbol.KIND);
          if (pSymbol.isPresent()) {
            if (pSymbol.get().isIncoming()) {
              Log.error("0xAA430 Did not find matching Variable or Output with name "
                  + currentNameToResolve, assign.get_SourcePositionStart());
            }
            else {
              checkAssignment(assign, pSymbol.get().getTypeReference(), currentNameToResolve);
            }
          }
          else if (vSymbol.isPresent()) {
            checkAssignment(assign, vSymbol.get().getTypeReference(), currentNameToResolve);
          }
        }
      }
    }
  }
  
  private void checkAssignment(ASTIOAssignment assign,
      JTypeReference<? extends JTypeSymbol> typeRef, String currentNameToResolve) {
    JTypeReference<? extends JTypeSymbol> varType = typeRef;
    try {
      for (ASTValuation val : assign.getValueList().get().getAllValuations()) {
        Optional<? extends JavaTypeSymbolReference> exprType = TypeCompatibilityChecker
            .getExpressionType(val.getExpression());
        if (!exprType.isPresent()) {
          Log.error(
              "0xAA433 Could not resolve type of expression for checking the reaction.",
              assign.get_SourcePositionStart());
        }
        else if (!TypeCompatibilityChecker.doTypesMatch(exprType.get(), varType)) {
          Log.error("0xAA431 Type of Variable/Output \"" + currentNameToResolve
              + "\" in the reaction does not match the type of its assigned expression. Type "
              +
              exprType.get().getName() + " can not cast to type " + varType.getName()
              + ".", val.get_SourcePositionStart());
        }
      }
    }
    catch (Exception e) {
      Log.error("0xAA432 Could not resolve type for checking the reaction.",
          assign.get_SourcePositionStart());
    }
  }
  
}
