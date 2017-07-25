package de.monticore.lang.montiarc.cocos.automaton.correctness;

import java.util.Optional;

import de.monticore.java.javadsl._ast.ASTExpression;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.lang.montiarc.helper.TypeCompatibilityChecker;
import de.monticore.lang.montiarc.montiarc._ast.ASTIOAssignment;
import de.monticore.lang.montiarc.montiarc._ast.ASTInitialStateDeclaration;
import de.monticore.lang.montiarc.montiarc._ast.ASTValuation;
import de.monticore.lang.montiarc.montiarc._cocos.MontiArcASTInitialStateDeclarationCoCo;
import de.monticore.lang.montiarc.montiarc._symboltable.VariableSymbol;
import de.monticore.lang.montiarc.montiarc._symboltable.VariableSymbol.Direction;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

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
public class InitialReactionTypeDoesNotFitOutputType implements MontiArcASTInitialStateDeclarationCoCo {
  
  @Override
  public void check(ASTInitialStateDeclaration node) {
    if (node.getBlock().isPresent()) {
      for (ASTIOAssignment assign : node.getBlock().get().getIOAssignments()) {
        if (assign.getName().isPresent()) {
          String currentNameToResolve = assign.getName().get();
          
          Optional<VariableSymbol> symbol = node.getEnclosingScope().get().resolve(currentNameToResolve, VariableSymbol.KIND);
          
          if (symbol.isPresent()) {
            if (symbol.get().getDirection() == Direction.Input) {
              Log.error("0xAA410 Did not find matching Variable or Output with name " + currentNameToResolve, assign.get_SourcePositionStart());
            }
            else {
              // We now have the field with the type of the variable/output
              if (assign.valueListIsPresent()) {
                JTypeReference<? extends JTypeSymbol> varType = symbol.get().getTypeReference();
                for (ASTValuation val : assign.getValueList().get().getAllValuations()) {
                  Optional<? extends JavaTypeSymbolReference> exprType = TypeCompatibilityChecker.getExpressionType((ASTExpression) val);
                  if (!exprType.isPresent()) {
                    Log.error("0xAA412 Could not resolve type of expression for checking the initial reaction.", assign.get_SourcePositionStart());
                  }
                  else if (!TypeCompatibilityChecker.doTypesMatch(exprType.get(), varType)) {
                    Log.error("0xAA411 Type of Variable/Output " + currentNameToResolve + " in the initial state declaration does not match the type of its assigned expression. Type " + 
                          exprType.get().getName() + " can not cast to type " + varType.getName() + ".", val.get_SourcePositionStart());
                  }
                }
              }
            }
          }
        }
      }
    }
  }
  
}
