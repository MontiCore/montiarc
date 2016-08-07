package de.monticore.automaton.ioautomatonjava.cocos.correctness;

import java.util.Optional;

import de.monticore.automaton.ioautomaton.TypeCompatibilityChecker;
import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._ast.ASTTransition;
import de.monticore.automaton.ioautomaton._ast.ASTValuationExt;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTTransitionCoCo;
import de.monticore.automaton.ioautomaton._symboltable.TransitionSymbol;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol.Direction;
import de.monticore.automaton.ioautomatonjava._ast.ASTValuation;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.FailedLoadingSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

/**
 * Context condition for checking, if all assignments inside a reaction of a transition
 * are type-correct.
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   $Version$
 *
 */
public class ReactionTypeDoesNotFitOutputType implements IOAutomatonASTTransitionCoCo {

  @Override
  public void check(ASTTransition node) {
    if (node.reactionIsPresent()) {
      for (ASTIOAssignment assign : node.getReaction().get().getIOAssignments()) {
        if (assign.nameIsPresent()) {
          String currentNameToResolve = assign.getName().get();
          
          Scope transitionScope = ((TransitionSymbol) node.getSymbol().get()).getSpannedScope();
          Optional<VariableSymbol> symbol = transitionScope.resolve(currentNameToResolve, VariableSymbol.KIND);
          
          if (symbol.isPresent()) {
            if (symbol.get().getDirection() == Direction.Input) {
              Log.error("0xAA430 Did not find matching Variable or Output with name " + currentNameToResolve, assign.get_SourcePositionStart());
            }
            else {
              // We now have the field with the type of the variable/output
              try {
                if (assign.valueListIsPresent()) {
                  JTypeReference<? extends JTypeSymbol> varType = symbol.get().getTypeReference();
                  for (ASTValuationExt val : assign.getValueList().get().getAllValuations()) {
                    if (!TypeCompatibilityChecker.doTypesMatch(((ASTValuation) val).getExpression(), varType)) {
                      Log.error("0xAA431 Type of Variable/Output \"" + currentNameToResolve + "\" in the reaction does not match the type of its assigned expression.", val.get_SourcePositionStart());
                    }
                  }
                }
              }
              catch (FailedLoadingSymbol e) {
                Log.error("0xAA432 Could not resolve type for checking the reaction.", assign.get_SourcePositionStart());
              }
            }
          }
        }
      }
    }
  }
  
}
