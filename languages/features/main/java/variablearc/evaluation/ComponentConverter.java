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
import de.monticore.types.check.CompKindExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._symboltable.ArcFeature2VariableAdapter;
import variablearc._symboltable.ArcFeatureSymbol;
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

    // convert parents
    for (CompKindExpression typeExpression : componentTypeSymbol.getTypeInfo().getSuperComponentsList()) {
      if (!visited.contains(typeExpression.getTypeInfo())) {
        expressionSet.add(convert(typeExpression, typeExpression.getTypeInfo().getName(), visited, true));
      }
    }

    // convert subcomponents
    for (SubcomponentSymbol instanceSymbol : componentTypeSymbol.getTypeInfo().getSubcomponents()) {
      if (instanceSymbol.isTypePresent() && !visited.contains(instanceSymbol.getType().getTypeInfo())) {
        expressionSet.add(convert(instanceSymbol.getType(), instanceSymbol.getName(), visited, false));
      }
    }

    return expressionSet;
  }

  protected ExpressionSet convert(@NotNull CompKindExpression typeExpression, @NotNull String prefix, @NotNull Collection<ComponentTypeSymbol> visited, boolean featuresToLocalSpace) {
    Preconditions.checkNotNull(typeExpression);
    Preconditions.checkNotNull(prefix);
    Preconditions.checkNotNull(visited);

    ArrayList<Expression> expressions = new ArrayList<>();

    if (typeExpression.getTypeInfo().getSpannedScope() instanceof IVariableArcScope) {
      // Convert parameters
      for (VariableSymbol variable : typeExpression.getTypeInfo().getParameters()) {
        Optional<ASTExpression> bindingExpression = typeExpression.getParamBindingFor(variable);

        // can only use default parameter value if ASTNode exists
        if (typeExpression.getTypeInfo().isPresentAstNode()) {
          final ASTComponentHead componentHead = ((ASTComponentType) typeExpression.getTypeInfo().getAstNode()).getHead();
          bindingExpression = bindingExpression.or(() -> componentHead.streamArcParameters().filter(param -> Objects.equals(param.getName(), variable.getName()) && param.isPresentDefault()).findAny().map(ASTArcParameter::getDefault));
        }

        bindingExpression.ifPresent(
          expr -> expressions.add(new AssignmentExpression(expr, variable, prefix)));
      }
      // Only applicable to inheritance, bind parent features to local features
      if (featuresToLocalSpace) {
        for (ArcFeatureSymbol feature : ((IVariableArcScope) typeExpression.getTypeInfo().getSpannedScope()).getLocalArcFeatureSymbols()) {
          ASTExpression expr = VariableArcMill.nameExpressionBuilder().setName(feature.getName()).build();
          expr.setEnclosingScope(feature.getEnclosingScope());
          expressions.add(new AssignmentExpression(expr, new ArcFeature2VariableAdapter(feature), prefix));
        }
      }
    }

    // add constraints
    ExpressionSet expressionSet = new ExpressionSet(expressions);
    if (typeExpression.getTypeInfo() instanceof IVariableArcComponentTypeSymbol) {
      expressionSet.add(((IVariableArcComponentTypeSymbol) typeExpression.getTypeInfo()).getConstraints(visited)
        .copyAddPrefix(prefix));
    }

    return expressionSet;
  }
}
