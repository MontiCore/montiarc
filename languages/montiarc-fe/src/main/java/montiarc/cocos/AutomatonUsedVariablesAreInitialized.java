/* (c) https://github.com/MontiCore/monticore */
package montiarc.cocos;

import com.google.common.collect.Lists;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.*;
import montiarc._cocos.MontiArcASTAutomatonCoCo;
import montiarc._symboltable.VariableSymbol;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor;

import java.util.*;

/**
 * Checks whether variables used on the right-hand side of assignments in transition reactions
 * are initialized.
 *
 * @implements No literature reference
 */
public class AutomatonUsedVariablesAreInitialized implements MontiArcASTAutomatonCoCo {
  private List<String> variables;
  private Stack<String> visitedStates;
  private Stack<String> assignedVariables;

  @Override
  public void check(ASTAutomaton node) {
    variables = new ArrayList<>();
    visitedStates = new Stack<>();
    assignedVariables = new Stack<>();

    if (node.getEnclosingScopeOpt().isPresent()
        && node.getEnclosingScopeOpt().get().getEnclosingScope().isPresent()) {
      node.getEnclosingScopeOpt().get().getEnclosingScope().get().resolveLocally(VariableSymbol.KIND)
          .forEach(varSymbol -> variables.add(varSymbol.getName()));

      checkStartingWithInitialState(node);
    } else {
      Log.error("0xMA010 ASTAutomaton has no no enclosing scope. Did you forget to run " +
                  "the SymbolTableCreator before checking cocos?");
    }
  }

  private void checkStartingWithInitialState(ASTAutomaton node) {
    assert visitedStates.size() == 0;
    final int pre_numInitializedVariables = assignedVariables.size();

    // Start check from each initial node.
    for (ASTInitialStateDeclaration initialStateDeclaration : node.getInitialStateDeclarationList()) {
      for (String initialState : initialStateDeclaration.getNameList()) {
        // Add initial state to nodes visited on current path.
        visitedStates.push(initialState);

        // Check initial assignments if they exist.
        if (initialStateDeclaration.isPresentBlock()) {
          for (ASTIOAssignment assignment : initialStateDeclaration.getBlock().getIOAssignmentList()) {
            // Check that all variables used on rhs are initialized.
            checkNoUninitializedVariable(assignment);
            // Add variable on lhs to assigned variables.
            visitAssignment(assignment);
          }
        }

        // Start recursive check from initial node.
        checkContinuingAlongTransitions(node);

        // Remove variable on lhs from assigned variables.
        if (initialStateDeclaration.isPresentBlock()) {
          for (ASTIOAssignment assignment :
              Lists.reverse(initialStateDeclaration.getBlock().getIOAssignmentList())) {
            endVisitAssignment(assignment);
          }
        }
        assert assignedVariables.size() == pre_numInitializedVariables;

        // Remove initial state from nodes visited on current path.
        assert visitedStates.peek().equals(initialState);
        visitedStates.pop();
        assert visitedStates.size() == 0;
      }
    }
  }

  private void checkContinuingAlongTransitions(ASTAutomaton node) {
    assert visitedStates != null;
    assert visitedStates.size() > 0;
    final int pre_numAssignedVariables = assignedVariables.size();

    // Continue check along each transition from current state.
    for (ASTTransition transition : node.getTransitionList()) {
      if (transition.getSource().equals(visitedStates.peek())) {

        // Check assignments in transition reaction if they exist.
        if (transition.isPresentReaction()) {
          for (ASTIOAssignment assignment : transition.getReaction().getIOAssignmentList()) {
            // Check that all variables used on rhs are initialized.
            checkNoUninitializedVariable(assignment);
            // Add variable on lhs to assigned variables.
            visitAssignment(assignment);
          }
        }

        // Take transition if target node not on current path and recursively continue from there.
        if (!visitedStates.contains(transition.getTargetOpt().orElse(transition.getSource()))) {
          // Add target node to nodes visited on current path.
          visitedStates.push(transition.getTargetOpt().orElse(transition.getSource()));

          // Recur from target node.
          checkContinuingAlongTransitions(node);

          // Remove target node from nodes visited on current path.
          assert visitedStates.peek().equals(transition.getTargetOpt().orElse(transition.getSource()));
          visitedStates.pop();
        }

        // Remove variable on lhs from assigned variables.
        if (transition.isPresentReaction()) {
          for (ASTIOAssignment assignment :
              Lists.reverse(transition.getReaction().getIOAssignmentList())) {
            endVisitAssignment(assignment);
          }
        }
        assert assignedVariables.size() == pre_numAssignedVariables;
      }
    }
  }

  private void endVisitAssignment(ASTIOAssignment assignment) {
    if (assignment.isAssignment()) {
      if (variables.contains(assignment.getName())) {
        assert assignedVariables.peek().equals(assignment.getName());
        assignedVariables.pop();
      }
    }
  }

  private void visitAssignment(ASTIOAssignment assignment) {
    if (assignment.isAssignment()) {
      if (variables.contains(assignment.getName())) {
        assignedVariables.push(assignment.getName());
      }
    }
  }

  private void checkNoUninitializedVariable(ASTIOAssignment assignment) {
    for (String variable : calculateUsedVariables(assignment)) {
      if (!assignedVariables.contains(variable)) {
        Log.error(assignment.get_SourcePositionStart() + ": 0xMA122 Variable " + variable +
            " may not be initialized.");
      }
    }
  }

  private List<String> calculateUsedVariables (ASTIOAssignment assignment) {
    List<String> result = new ArrayList<>();

    // Use visitor to calculate all name usages in assignment expression.
    NamesInExpressionsDelegatorVisitor ev = new NamesInExpressionsDelegatorVisitor();
    if (assignment.isPresentValueList() && assignment.getValueList().isPresentValuation()) {
      assignment.getValueList().getValuation().getExpression().accept(ev);
      Map<ASTNameExpression, NamesInExpressionsDelegatorVisitor.ExpressionKind> foundNames
          = ev.getFoundNames();
      for (Map.Entry<ASTNameExpression, NamesInExpressionsDelegatorVisitor.ExpressionKind>
          entry : foundNames.entrySet()) {
        // If found name is a variable then add it to the result list
        if (variables.contains(entry.getKey().getName())) {
          result.add(entry.getKey().getName());
        }
      }
    }
    return result;
  }
}