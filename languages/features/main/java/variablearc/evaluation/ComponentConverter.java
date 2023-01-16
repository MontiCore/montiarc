/*
  (c) https://github.com/MontiCore/monticore
 */

package variablearc.evaluation;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc.check.TypeExprOfVariableComponent;
import variablearc.check.VariableArcTypeCalculator;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

import java.util.*;
import java.util.stream.Collectors;

public class ComponentConverter {

  final Context context;

  public ComponentConverter(Context context) {
    this.context = context;
  }

  public List<BoolExpr> convert(@NotNull ComponentTypeSymbol componentTypeSymbol) {
    Preconditions.checkNotNull(componentTypeSymbol);

    return convert(componentTypeSymbol, new Stack<>());
  }

  protected List<BoolExpr> convert(@NotNull ComponentTypeSymbol componentTypeSymbol, @NotNull Stack<String> prefixes) {
    Preconditions.checkNotNull(componentTypeSymbol);
    Preconditions.checkNotNull(prefixes);

    IDeriveSMTExpr converter = VariableArcMill.fullConverter(context);
    converter.setPrefix(listToString(prefixes));

    // convert constraints
    ArrayList<BoolExpr> contextExpr = componentTypeSymbol.getAstNode().getBody()
        .getArcElementList().stream()
        .filter(e -> e instanceof ASTArcConstraintDeclaration)
        .map(e -> converter.toBool(((ASTArcConstraintDeclaration) e).getExpression()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toCollection(ArrayList::new));

    // convert subcomponents
    for (ComponentInstanceSymbol instanceSymbol : componentTypeSymbol.getSubComponents()) {
      prefixes.add(instanceSymbol.getName());
      contextExpr.addAll(convert(instanceSymbol.getType(), prefixes));
      prefixes.pop();
    }

    return contextExpr;
  }

  public List<BoolExpr> convert(@NotNull CompTypeExpression compTypeExpression) {
    Preconditions.checkNotNull(compTypeExpression);
    Stack<String> prefixes = new Stack<>();
    prefixes.add(compTypeExpression.printName());

    return convert(compTypeExpression, prefixes);
  }

  protected List<BoolExpr> convert(@NotNull CompTypeExpression compTypeExpression, Stack<String> prefixes) {
    Preconditions.checkNotNull(compTypeExpression);
    Preconditions.checkNotNull(prefixes);

    IDeriveSMTExpr converter = VariableArcMill.fullConverter(context);
    ArrayList<BoolExpr> contextExpr = new ArrayList<>();
    final String prefix = listToString(prefixes);

    // Convert features & parameters
    if (compTypeExpression instanceof TypeExprOfVariableComponent && !prefixes.isEmpty()) {
      String parentPrefix = prefixes.size() > 1 ? listToString(prefixes.subList(0, prefixes.size() - 1)) : "";
      converter.setPrefix(parentPrefix);
      TypeExprOfVariableComponent variableCompTypeExpression = (TypeExprOfVariableComponent) compTypeExpression;

      // Convert parameters
      for (VariableSymbol variable : compTypeExpression.getTypeInfo().getSpannedScope().getLocalVariableSymbols()) {
        Optional<ASTExpression> bindingExpression = variableCompTypeExpression.getBindingFor(variable);
        Optional<Expr<?>> bindingSolverExpression = bindingExpression.flatMap(converter::toExpr);

        Optional<Expr<?>> nameExpression =
            (new VariableArcDeriveSMTSort(new VariableArcTypeCalculator())).toSort(context, variable.getType())
                .map(s -> context.mkConst(prefix + "." + variable.getName(), s));
        if (bindingExpression.isPresent() && bindingSolverExpression.isPresent() &&
            nameExpression.isPresent()) {
          contextExpr.add(context.mkEq(nameExpression.get(), bindingSolverExpression.get()));
        }
      }
      // Convert features
      for (ArcFeatureSymbol feature : ((IVariableArcScope) compTypeExpression.getTypeInfo()
          .getSpannedScope()).getLocalArcFeatureSymbols()) {
        Optional<ASTExpression> bindingExpression = variableCompTypeExpression.getBindingFor(feature);
        if (bindingExpression.isPresent()) {
          Optional<Expr<?>> solverExpression = bindingExpression.flatMap(converter::toExpr);
          if (solverExpression.isPresent() && solverExpression.get().isBool()) {
            contextExpr.add(context.mkEq(context.mkBoolConst(prefix + "." + feature.getName()),
                solverExpression.get()));
          }
        } else {
          // default to false for unassigned features
          contextExpr.add(context.mkEq(context.mkBoolConst(prefix + "." + feature.getName()),
              context.mkBool(false)));
        }
      }
    }
    // convert constraints
    converter.setPrefix(prefix);
    contextExpr.addAll(
        compTypeExpression.getTypeInfo().getAstNode().getBody()
            .getArcElementList().stream()
            .filter(e -> e instanceof ASTArcConstraintDeclaration)
            .map(e -> converter.toBool(((ASTArcConstraintDeclaration) e).getExpression()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList())
    );
    // convert subcomponents
    for (ComponentInstanceSymbol instanceSymbol : compTypeExpression.getTypeInfo().getSubComponents()) {
      prefixes.add(instanceSymbol.getName());
      contextExpr.addAll(convert(instanceSymbol.getType(), prefixes));
      prefixes.pop();
    }


    return contextExpr;
  }

  protected String listToString(List<String> list) {
    return list.stream().reduce((a, b) -> a + "." + b).orElse("");
  }
}
