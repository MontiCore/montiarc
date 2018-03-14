package montiarc.cocos;

import java.util.Optional;

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
 * Context condition for checking, if all assignments inside a stimulus of a transition are
 * type-correct.
 *
 * @author Andreas Wortmann
 */
public class AutomatonStimulusTypeDoesNotFitInputType implements MontiArcASTTransitionCoCo {
  
  @Override
  public void check(ASTTransition node) {
    if (node.isStimulusPresent()) {
      for (ASTIOAssignment assign : node.getStimulus().getIOAssignmentList()) {
        if (assign.isNamePresent()) {
          String currentNameToResolve = assign.getName();
          
          Scope transitionScope = ((TransitionSymbol) node.getSymbol().get()).getSpannedScope();
          Optional<VariableSymbol> varSymbol = transitionScope.resolve(currentNameToResolve,
              VariableSymbol.KIND);
          Optional<PortSymbol> portSymbol = node.getEnclosingScope().get()
              .resolve(currentNameToResolve, PortSymbol.KIND);
          
          if (portSymbol.isPresent()) {
            if (portSymbol.get().isOutgoing()) {
              Log.error("0xMA045 Did not find matching Variable or Input with name "
                  + currentNameToResolve, assign.get_SourcePositionStart());
            }
            else {
              checkAssignment(assign, portSymbol.get().getTypeReference(), currentNameToResolve);
            }
          }
          else if (varSymbol.isPresent()) {
            checkAssignment(assign, varSymbol.get().getTypeReference(), currentNameToResolve);
          }
        }
      }
    }
  }
  
  private void checkAssignment(ASTIOAssignment assign,
      JTypeReference<? extends JTypeSymbol> typeRef, String currentNameToResolve) {
    if (assign.isValueListPresent()) {
      JTypeReference<? extends JTypeSymbol> varType = typeRef;
      for (ASTValuation val : assign.getValueList().getAllValuations()) {
        Optional<? extends JavaTypeSymbolReference> exprType = TypeCompatibilityChecker
            .getExpressionType(val.getExpression());
        if (!exprType.isPresent()) {
          Log.error("0xMA048 Could not resolve type of expression for checking the stimulus.",
              assign.get_SourcePositionStart());
        }
        else if (!TypeCompatibilityChecker.doTypesMatch(exprType.get(), varType)) {
          Log.error(
              "0xMA046 Type of Variable/Input " + currentNameToResolve
                  + " in the stimulus does not match the type of its assigned expression. Type " +
                  exprType.get().getName() + " can not cast to type " + varType.getName() + ".",
              val.get_SourcePositionStart());
        }
      }
    }
  }
  
}
