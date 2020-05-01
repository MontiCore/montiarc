/* (c) https://github.com/MontiCore/monticore */
package dynamicmontiarc.cocos;

import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.references.SymbolReference;
import de.monticore.symboltable.types.JFieldSymbol;
import de.se_rwth.commons.logging.Log;
import dynamicmontiarc._ast.ASTModeTransition;
import dynamicmontiarc._cocos.DynamicMontiArcASTModeTransitionCoCo;
import montiarc._ast.ASTIOAssignment;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor;
import montiarc.visitor.NamesInExpressionsDelegatorVisitor.ExpressionKind;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Checks that the requirements for mode transitions reaction and guard are
 * fulfilled.
 * For the guard, it is only allowed to use incoming ports and component variables.
 * In the reaction only assignments to component variables are allowed.
 *
 */
public class ModeTransitionCorrectVarAndPortUsage implements DynamicMontiArcASTModeTransitionCoCo {

  @Override
  public void check(ASTModeTransition node) {

    Scope componentScope = node.getEnclosingScopeOpt().get()
                               .getEnclosingScope().get();

    // check reaction
    if (node.isPresentReaction()) {

      // Check all IOAssignments in the reaction
      for (ASTIOAssignment reactionExpr : node.getReaction().getIOAssignmentList()) {
        if (reactionExpr.isAssignment()) {
          // check left hand side of assignment
          final String assignee = reactionExpr.getName();
          Optional<VariableSymbol> variable =
          componentScope.resolve(assignee, VariableSymbol.KIND);
          if(!variable.isPresent()){
            Log.error(
                String.format("0xMA214 Only variables are allowed to be " +
                                  "assigned in the reaction of a mode " +
                                  "transition. The identifier %s is not " +
                                  "a variable.",
                    assignee),
                reactionExpr.get_SourcePositionStart());
          }
        }

        // No function calls allowed without assignment to variable
        if(reactionExpr.isCall()){
          Log.error(
              "0xMA213 Function calls are not allowed in mode " +
                  "transitions. Only variable assignments are permitted.",
              reactionExpr.get_SourcePositionStart());
        }

        if (reactionExpr.isPresentValueList() &&
                reactionExpr.getValueList().isPresentValuation()) {
          checkValueListWithValuation(componentScope,
              reactionExpr.getValueList().getValuation().getExpression());
        }
      }
    }

    // Check guard
    if(node.isPresentGuard()){
      final ASTExpression expression =
          node.getGuard().getGuardExpression().getExpression();

      checkValueListWithValuation(componentScope, expression);
    }
  }

  /**
   *
   * @param componentScope
   * @param expression
   */
  private void checkValueListWithValuation(
      Scope componentScope,
      ASTExpression expression) {

    final Optional<? extends ScopeSpanningSymbol> component = componentScope.getSpanningSymbol();
    if(!component.isPresent()){
      Log.warn("Could not retrieve component symbol");
      return;
    }

    ComponentSymbol symbol = (ComponentSymbol) component.get();

    // Find the names used in the expression

    NamesInExpressionsDelegatorVisitor ev =
        new NamesInExpressionsDelegatorVisitor();
    expression.accept(ev);

    Map<ASTNameExpression, ExpressionKind> foundNames = ev.getFoundNames();

    final Set<String> parameterNames =
        symbol.getConfigParameters().stream()
            .map(JFieldSymbol::getName)
        .collect(Collectors.toSet());

    for (ASTNameExpression entry : foundNames.keySet()) {

      Optional<PortSymbol> port =
          componentScope.resolve(entry.getName(), PortSymbol.KIND);

      if(parameterNames.contains(entry.getName())){
        Log.error("0xMA215 Parameter usage is not allowed in " +
                      "mode transitions.",
            entry.get_SourcePositionStart());
      }

      if (port.isPresent()) {
        if (port.get().isOutgoing()) {
          Log.error(
              String.format(
                  "0xMA216 Outgoing port %s must not be used in right " +
                      "hand side of reaction.",
                  entry.getName()),
              entry.get_SourcePositionStart());
        }
      }
    }
  }
}
