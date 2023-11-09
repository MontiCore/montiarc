/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentHead;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcScope;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc.evaluation.expressions.AssignmentExpression;
import variablearc.evaluation.expressions.Expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

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
   * @param visited             already visited components (used to detect circles)
   * @return the expression set of all the component's constraints
   */
  public ExpressionSet convert(@NotNull IVariableArcComponentTypeSymbol componentTypeSymbol, @NotNull Collection<ComponentTypeSymbol> visited) {
    Preconditions.checkNotNull(componentTypeSymbol);
    Preconditions.checkNotNull(visited);

    // convert constraints
    ExpressionSet expressionSet = componentTypeSymbol.getLocalConstraints().copy();

    // convert subcomponents
    for (ComponentInstanceSymbol instanceSymbol : componentTypeSymbol.getTypeInfo().getSubComponents()) {
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

    // Convert parameters
    for (VariableSymbol variable : componentInstanceSymbol.getType().getTypeInfo().getParameters()) {
      Optional<ASTExpression> bindingExpression = componentInstanceSymbol.getType().getParamBindingFor(variable).map(ASTArcArgument::getExpression);

      // can only use default parameter value if ASTNode exists
      if (componentInstanceSymbol.getType().getTypeInfo().isPresentAstNode()) {
        final ASTComponentHead componentHead = componentInstanceSymbol.getType().getTypeInfo().getAstNode().getHead();
        bindingExpression = bindingExpression.or(() -> componentHead.streamArcParameters().filter(param -> Objects.equals(param.getName(), variable.getName()) && param.isPresentDefault()).findAny().map(ASTArcParameter::getDefault));
      }

      bindingExpression.ifPresent(
        expr -> expressions.add(new AssignmentExpression(expr, variable, prefix)));
    }

    // add constraints
    ExpressionSet expressionSet = new ExpressionSet(expressions);
    expressionSet.add(((IVariableArcComponentTypeSymbol) componentInstanceSymbol.getType().getTypeInfo()).getConstraints(visited)
      .copyAddPrefix(prefix));

    return expressionSet;
  }
}
