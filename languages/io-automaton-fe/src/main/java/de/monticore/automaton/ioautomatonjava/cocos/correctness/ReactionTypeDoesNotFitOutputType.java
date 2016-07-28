package de.monticore.automaton.ioautomatonjava.cocos.correctness;

import java.util.Optional;

import de.monticore.automaton.ioautomaton._ast.ASTAutomaton;
import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._ast.ASTTransition;
import de.monticore.automaton.ioautomaton._ast.ASTValuationExt;
import de.monticore.automaton.ioautomaton._ast.ASTValueList;
import de.monticore.automaton.ioautomaton._cocos.IOAutomatonASTAutomatonCoCo;
import de.monticore.automaton.ioautomaton._symboltable.AutomatonSymbol;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol;
import de.monticore.automaton.ioautomaton._symboltable.VariableSymbol.Direction;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.FailedLoadingSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
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
public class ReactionTypeDoesNotFitOutputType implements IOAutomatonASTAutomatonCoCo {

  @Override
  public void check(ASTAutomaton node) {
    // For each Reaction, compare the type of the expression with the type of
    // the field assigned
    if (node.getAutomatonContent().getTransitions() != null) {
      for (ASTTransition t : node.getAutomatonContent().getTransitions()) {
        if (t.getReaction().isPresent()) {
          for (ASTIOAssignment assign : t.getReaction().get().getIOAssignments()) {
            Optional<String> currentNameToResolve = assign.getName();
            if (currentNameToResolve.isPresent()) {
              // Get IOFieldEntry
              Scope automatonScope = ((AutomatonSymbol) node.getSymbol().get()).getSpannedScope();
              Optional<VariableSymbol> symbol = automatonScope.resolve(currentNameToResolve.get(), VariableSymbol.KIND);
              
              if (symbol.isPresent()) {
                if (symbol.get().getDirection() == Direction.Input) {
                  Log.error("0xAA404 Did not find matching Variable or Output with name " + currentNameToResolve.get(), assign.get_SourcePositionStart());
                }
                else {
                  // We now have the field with the type of the variable/output
                  // Get the type
                  try {
                    JTypeSymbol type =  symbol.get().getTypeReference().getReferencedSymbol();
                    if (assign.getValueList().isPresent()) {
                      ASTValueList vl = assign.getValueList().get();
                      for (ASTValuationExt n : vl.getValuations()) {
                        // TODO
//                      if (!ExpressionHelper.compareExpressionTypeWithType((ASTExpression) n, type, node, checker, this.getResolver().getModelloader(), this.deserializers)) {
//                        addReport("Type of Variable/Output " + currentNameToResolve + " in the reaction does not match the type of its assigned expression.", n.get_SourcePositionStart());
//                      }
                      }
                    }
                  } catch (FailedLoadingSymbol e) {
                    Log.error("0xAA404 Could not resolve type for checking the reaction.", assign.get_SourcePositionStart());
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
