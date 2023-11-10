/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentHead;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._symboltable.IVariableArcComponentTypeSymbol;
import variablearc._symboltable.IVariableArcScope;
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
    for (SubcomponentSymbol instanceSymbol : componentTypeSymbol.getTypeInfo().getSubcomponents()) {
      if (instanceSymbol.isTypePresent() && !visited.contains(instanceSymbol.getType().getTypeInfo())) {
        expressionSet.add(convert(instanceSymbol, instanceSymbol.getName(), visited));
      }
    }

    return expressionSet;
  }

  protected ExpressionSet convert(@NotNull SubcomponentSymbol subcomponentSymbol, @NotNull String prefix, @NotNull Collection<ComponentTypeSymbol> visited) {
    Preconditions.checkNotNull(subcomponentSymbol);
    Preconditions.checkArgument(subcomponentSymbol.isTypePresent());
    Preconditions.checkNotNull(prefix);
    Preconditions.checkNotNull(visited);

    ArrayList<Expression> expressions = new ArrayList<>();

    if (subcomponentSymbol.getType().getTypeInfo().getSpannedScope() instanceof IVariableArcScope) {
      // Convert parameters
      for (VariableSymbol variable : subcomponentSymbol.getType().getTypeInfo().getParametersList()) {
        Optional<ASTExpression> bindingExpression = subcomponentSymbol.getType().getParamBindingFor(variable);

        // can only use default parameter value if ASTNode exists
        if (subcomponentSymbol.getType().getTypeInfo().isPresentAstNode()) {
          final ASTComponentHead componentHead = ((ASTComponentType) subcomponentSymbol.getType().getTypeInfo().getAstNode()).getHead();
          bindingExpression = bindingExpression.or(() -> componentHead.streamArcParameters().filter(param -> Objects.equals(param.getName(), variable.getName()) && param.isPresentDefault()).findAny().map(ASTArcParameter::getDefault));
        }

        bindingExpression.ifPresent(
          expr -> expressions.add(new AssignmentExpression(expr, variable, prefix)));
      }
    }

    // add constraints
    ExpressionSet expressionSet = new ExpressionSet(expressions);
    if (subcomponentSymbol.getType().getTypeInfo() instanceof IVariableArcComponentTypeSymbol) {
      expressionSet.add(((IVariableArcComponentTypeSymbol) subcomponentSymbol.getType().getTypeInfo()).getConstraints(visited)
        .copyAddPrefix(prefix));
    }

    return expressionSet;
  }
}
