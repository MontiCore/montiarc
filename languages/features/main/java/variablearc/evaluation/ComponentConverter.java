/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._ast.ASTArcArgument;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._ast.ASTArcConstraintDeclaration;
import variablearc._symboltable.IVariableArcScope;
import variablearc.check.VariableArcTypeCalculator;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Converts a component's constraints into solvable SAT formulas.
 * This can be used to evaluate other expressions (e.g. if-statement conditions)
 * in the context of this component.
 * <p>
 * During conversion subcomponents' constraints are converted by assigning them a prefix.
 */
public class ComponentConverter {

  final Context context;

  public ComponentConverter(Context context) {
    this.context = context;
  }

  public List<BoolExpr> convert(@NotNull ComponentTypeSymbol componentTypeSymbol) {
    Preconditions.checkNotNull(componentTypeSymbol);

    return convert(componentTypeSymbol, new Stack<>(), new HashSet<>());
  }

  protected List<BoolExpr> convert(@NotNull ComponentTypeSymbol componentTypeSymbol, @NotNull Stack<String> prefixes, @NotNull Collection<ComponentTypeSymbol> visited) {
    Preconditions.checkNotNull(componentTypeSymbol);
    Preconditions.checkNotNull(prefixes);
    Preconditions.checkNotNull(visited);
    visited.add(componentTypeSymbol);

    IDeriveSMTExpr converter = VariableArcMill.fullConverter(context);
    converter.setPrefix(listToString(prefixes));

    // convert constraints
    ArrayList<BoolExpr> contextExpr = new ArrayList<>();
    if (componentTypeSymbol.isPresentAstNode()) {
      contextExpr = componentTypeSymbol.getAstNode().getBody()
        .getArcElementList().stream()
        .filter(e -> e instanceof ASTArcConstraintDeclaration)
        .map(e -> converter.toBool(((ASTArcConstraintDeclaration) e).getExpression()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toCollection(ArrayList::new));
    }

    // convert subcomponents
    for (ComponentInstanceSymbol instanceSymbol : componentTypeSymbol.getSubComponents()) {
      if (instanceSymbol.isPresentType() && !visited.contains(instanceSymbol.getType().getTypeInfo())) {
        prefixes.add(instanceSymbol.getName());
        contextExpr.addAll(convert(instanceSymbol, prefixes, visited));
        prefixes.pop();
      }
    }

    return contextExpr;
  }

  protected List<BoolExpr> convert(@NotNull ComponentInstanceSymbol componentInstanceSymbol, Stack<String> prefixes, @NotNull Collection<ComponentTypeSymbol> visited) {
    Preconditions.checkNotNull(componentInstanceSymbol);
    Preconditions.checkArgument(componentInstanceSymbol.isPresentType());
    Preconditions.checkNotNull(prefixes);
    Preconditions.checkNotNull(visited);

    IDeriveSMTExpr converter = VariableArcMill.fullConverter(context);
    ArrayList<BoolExpr> contextExpr = new ArrayList<>();
    final String prefix = listToString(prefixes);

    if (componentInstanceSymbol.getType().getTypeInfo().getSpannedScope() instanceof IVariableArcScope) {
      // Convert features & parameters
      String parentPrefix = prefixes.size() > 1 ? listToString(prefixes.subList(0, prefixes.size() - 1)) : "";
      converter.setPrefix(parentPrefix);

      // Convert parameters
      for (VariableSymbol variable : componentInstanceSymbol.getType().getTypeInfo().getSpannedScope()
        .getLocalVariableSymbols()) {
        Optional<ASTArcArgument> bindingExpression = componentInstanceSymbol.getType().getParamBindingFor(variable);
        Optional<Expr<?>> bindingSolverExpression =
          bindingExpression.map(ASTArcArgument::getExpression).flatMap(converter::toExpr);

        Optional<Expr<?>> nameExpression =
          (new VariableArcDeriveSMTSort(new VariableArcTypeCalculator())).toSort(context, variable.getType())
            .map(s -> context.mkConst(prefix + "." + variable.getName(), s));
        if (bindingExpression.isPresent() && bindingSolverExpression.isPresent() &&
          nameExpression.isPresent()) {
          contextExpr.add(context.mkEq(nameExpression.get(), bindingSolverExpression.get()));
        }
      }
    }

    // convert component body
    contextExpr.addAll(convert(componentInstanceSymbol.getType().getTypeInfo(), prefixes, visited));
    return contextExpr;
  }

  protected String listToString(List<String> list) {
    return list.stream().reduce((a, b) -> a + "." + b).orElse("");
  }
}
