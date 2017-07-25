package de.monticore.lang.montiarc.cocos.automaton.correctness;

import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.lang.montiarc.helper.TypeCompatibilityChecker;
import de.monticore.lang.montiarc.montiarc._ast.ASTIOAssignment;
import de.monticore.lang.montiarc.montiarc._ast.ASTTransition;
import de.monticore.lang.montiarc.montiarc._ast.ASTValuation;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTTransitionCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.TransitionSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.VariableSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.VariableSymbol.Direction;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.FailedLoadingSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if all assignments inside a stimulus of a
 * transition are type-correct.
 *
 * @author Andreas Wortmann
 */
public class StimulusTypeDoesNotFitInputType implements MontiArcASTTransitionCoCo {
  
  @Override
  public void check(ASTTransition node) {
    if (node.stimulusIsPresent()) {
      for (ASTIOAssignment assign : node.getStimulus().get().getIOAssignments()) {
        if (assign.nameIsPresent()) {
          String currentNameToResolve = assign.getName().get();
          
          Scope transitionScope = ((TransitionSymbol) node.getSymbol().get()).getSpannedScope();
          Optional<VariableSymbol> symbol = transitionScope.resolve(currentNameToResolve, VariableSymbol.KIND);
          
          if (symbol.isPresent()) {
            if (symbol.get().getDirection() == Direction.Output) {
              Log.error("0xAA440 Did not find matching Variable or Input with name " + currentNameToResolve, assign.get_SourcePositionStart());
            }
            else {
              // We now have the field with the type of the variable/input
              try {
                if (assign.valueListIsPresent()) {
                  JTypeReference<? extends JTypeSymbol> varType = symbol.get().getTypeReference();
                  for (ASTValuation val : assign.getValueList().get().getAllValuations()) {     
                    Optional<? extends JavaTypeSymbolReference> exprType = TypeCompatibilityChecker.getExpressionType((ASTExpression) val);
                    if (!exprType.isPresent()) {
                      Log.error("0xAA443 Could not resolve type of expression for checking the stimulus.", assign.get_SourcePositionStart());
                    }
                    else if (!TypeCompatibilityChecker.doTypesMatch(exprType.get(), varType)) {
                      Log.error("0xAA441 Type of Variable/Input " + currentNameToResolve + " in the stimulus does not match the type of its assigned expression. Type " + 
                          exprType.get().getName() + " can not cast to type " + varType.getName() + ".", val.get_SourcePositionStart());
                    }
                  }
                }
              } catch (FailedLoadingSymbol e) {
                Log.error("0xAA442 Could not resolve type for checking the stimulus.", assign.get_SourcePositionStart());
              }
            }
          }
        }
      }
    }
  }
  
  
}
