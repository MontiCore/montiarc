package de.monticore.automaton.ioautomatonjava.cocos.correctness;

import java.util.Optional;

import de.monticore.automaton.ioautomaton.TypeCompatibilityChecker;
import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._ast.ASTInitialStateDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTValuationExt;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTInitialStateDeclarationCoCo;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol.Direction;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
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
public class InitialReactionTypeDoesNotFitOutputType implements IOAutomatonASTInitialStateDeclarationCoCo {
  
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
                for (ASTValuationExt val : assign.getValueList().get().getAllValuations()) {
                  Optional<? extends JavaTypeSymbolReference> exprType = TypeCompatibilityChecker.getExpressionType(val.getExpression());
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
