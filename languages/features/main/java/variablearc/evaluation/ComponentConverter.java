/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._ast.ASTArcArgument;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._symboltable.IVariableArcScope;
import variablearc._symboltable.VariableComponentTypeSymbol;
import variablearc.evaluation.expressions.AssignmentExpression;
import variablearc.evaluation.expressions.Expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Converts a component's constraints into solvable SAT formulas.
 * This can be used to evaluate other expressions (e.g. if-statement conditions)
 * in the context of this component.
 * <p>
 * During conversion subcomponents' constraints are converted by assigning them a prefix.
 */
public class ComponentConverter {

  /**
   * @param componentTypeSymbol the component which constraints are calculated
   * @param visited already visited components (used to detect circles)
   * @return the expression set of all the component's constraints
   */
  public ExpressionSet convert(@NotNull VariableComponentTypeSymbol componentTypeSymbol, @NotNull Collection<ComponentTypeSymbol> visited) {
    Preconditions.checkNotNull(componentTypeSymbol);
    Preconditions.checkNotNull(visited);
    visited.add(componentTypeSymbol);

    // convert constraints
    ExpressionSet expressionSet = componentTypeSymbol.getConstraints().copy();

    // convert subcomponents
    for (ComponentInstanceSymbol instanceSymbol : componentTypeSymbol.getSubComponents()) {
      if (instanceSymbol.isPresentType() && !visited.contains(instanceSymbol.getType().getTypeInfo())) {
        expressionSet.add(convert(instanceSymbol, instanceSymbol.getName(), visited));
      }
    }

    return expressionSet;
  }

  protected ExpressionSet convert(@NotNull ComponentInstanceSymbol componentInstanceSymbol, @NotNull String prefix, @NotNull Collection<ComponentTypeSymbol> visited) {
    Preconditions.checkNotNull(componentInstanceSymbol);
    Preconditions.checkArgument(componentInstanceSymbol.isPresentType());
    Preconditions.checkNotNull(prefix);
    Preconditions.checkNotNull(visited);

    ArrayList<Expression> expressions = new ArrayList<>();

    if (componentInstanceSymbol.getType().getTypeInfo().getSpannedScope() instanceof IVariableArcScope) {
      // Convert parameters
      for (VariableSymbol variable : componentInstanceSymbol.getType().getTypeInfo().getSpannedScope()
        .getLocalVariableSymbols()) {
        Optional<ASTArcArgument> bindingExpression = componentInstanceSymbol.getType().getParamBindingFor(variable);

        bindingExpression.ifPresent(
          astArcArgument -> expressions.add(new AssignmentExpression(astArcArgument.getExpression(), variable, prefix)));
      }
    }
    // add constraints
    ExpressionSet expressionSet = new ExpressionSet(expressions);
    expressionSet.add(((VariableComponentTypeSymbol) componentInstanceSymbol.getType().getTypeInfo()).getConditions(visited)
      .copyAddPrefix(prefix));

    return expressionSet;
  }
}
