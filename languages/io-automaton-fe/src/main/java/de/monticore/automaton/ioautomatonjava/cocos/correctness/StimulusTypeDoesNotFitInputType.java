package de.monticore.automaton.ioautomatonjava.cocos.correctness;

import java.util.Optional;
import de.monticore.automaton.ioautomaton.TypeCompatibilityChecker;
import de.monticore.automaton.ioautomaton._ast.ASTAutomaton;
import de.monticore.automaton.ioautomaton._ast.ASTIOAssignment;
import de.monticore.automaton.ioautomaton._ast.ASTTransition;
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
 * Context condition for checking, if all assignments inside a stimulus of a
 * transition are type-correct.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since $Version$
 */
public class StimulusTypeDoesNotFitInputType implements IOAutomatonASTAutomatonCoCo {
  
  @Override
  public void check(ASTAutomaton node) {
    
    // For each Stimulus, compare the type of the expression with the type of
    // the field assigned
    if (node.getAutomatonContent().getTransitions() != null) {
      for (ASTTransition t : node.getAutomatonContent().getTransitions()) {
        if (t.getStimulus().isPresent()) {
          for (ASTIOAssignment assign : t.getStimulus().get().getIOAssignments()) {
            Optional<String> currentNameToResolve = assign.getName();
            if (currentNameToResolve.isPresent()) {
              // Get IOFieldEntry
              Scope automatonScope = ((AutomatonSymbol) node.getSymbol().get()).getSpannedScope();
              Optional<VariableSymbol> symbol = automatonScope.resolve(currentNameToResolve.get(), VariableSymbol.KIND);
              
              if (symbol.isPresent()) {
                if (symbol.get().getDirection() == Direction.Output) {
                  Log.error("0xAA405 Did not find matching Variable or Input with name " + currentNameToResolve.get(), assign.get_SourcePositionStart());
                }
                else {
                  // We now have the field with the type of the variable/input
                  try {
                    if (assign.valueListIsPresent()) {
                      for (ASTValuationExt val : assign.getValueList().get().getAllValuations()) {
                        JTypeReference<JTypeSymbol> varType = symbol.get().getTypeReference();
                        
                        if (!TypeCompatibilityChecker.doTypesMatch(((ASTValuation)val).getExpression(), varType)) {
                          Log.error("0xAA405 Type of Variable/Input " + currentNameToResolve + " in the stimulus does not match the type of its assigned expression.", val.get_SourcePositionStart());
                        }
                      }
                    }
                  } catch (FailedLoadingSymbol e) {
                    Log.error("0xAA405 Could not resolve type for checking the stimulus.", assign.get_SourcePositionStart());
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
