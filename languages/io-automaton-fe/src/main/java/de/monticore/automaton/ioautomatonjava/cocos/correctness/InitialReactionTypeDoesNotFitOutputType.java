package de.monticore.automaton.ioautomatonjava.cocos.correctness;

import java.util.Optional;

import de.monticore.automaton.ioautomaton.TypeCompatibilityChecker;
import de.monticore.automaton.ioautomaton._ast.ASTAutomaton;
import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._ast.ASTInitialStateDeclaration;
import de.monticore.automaton.ioautomaton._ast.ASTValuationExt;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonCoCo;
import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol.Direction;
import de.monticore.automaton.ioautomatonjava._ast.ASTValuation;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.FailedLoadingSymbol;
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
public class InitialReactionTypeDoesNotFitOutputType implements IOAutomatonASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    for (ASTInitialStateDeclaration isd : node.getAutomatonContent().getInitialStateDeclarations()) {
      if (isd.getBlock().isPresent()) {
        for (ASTIOAssignment assign : isd.getBlock().get().getIOAssignments()) {
          if (assign.getName().isPresent()) {
            String currentNameToResolve = assign.getName().get();
            
            Scope automatonScope = ((AutomatonSymbol) node.getSymbol().get()).getSpannedScope();
            Optional<VariableSymbol> symbol = automatonScope.resolve(currentNameToResolve, VariableSymbol.KIND);
            
            if (symbol.isPresent()) {
              if (symbol.get().getDirection() == Direction.Input) {
                Log.error("0xAA410 Did not find matching Variable or Output with name " + currentNameToResolve, assign.get_SourcePositionStart());
              }
              else {
                // We now have the field with the type of the variable/output
                try {
                  if (assign.valueListIsPresent()) {
                    JTypeReference<? extends JTypeSymbol> varType = symbol.get().getTypeReference();
                    for (ASTValuationExt val : assign.getValueList().get().getAllValuations()) {
                      if (!TypeCompatibilityChecker.doTypesMatch(((ASTValuation) val).getExpression(), varType)) {
                        Log.error("0xAA411 Type of Variable/Output " + currentNameToResolve + " in the initial state declaration does not match the type of its assigned expression.", val.get_SourcePositionStart());
                      }
                    }
                  }
                }
                catch (FailedLoadingSymbol e) {
                  Log.error("0xAA412 Could not resolve type for checking initial reaction.", assign.get_SourcePositionStart());
                }
              }
            }
          }
        }
      }
    }
  }
  
}
