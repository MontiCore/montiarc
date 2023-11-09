/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.expressions;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;
import variablearc.VariableArcMill;
import variablearc.check.VariableArcTypeCalculator;
import variablearc.evaluation.VariableArcDeriveSMTSort;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

import java.util.Arrays;
import java.util.Optional;

public class AssignmentExpression extends Expression {

  protected VariableSymbol variable;

  public AssignmentExpression(@NotNull ASTExpression astExpression, @NotNull VariableSymbol variable) {
    this(astExpression, variable, null);
  }

  public AssignmentExpression(@NotNull ASTExpression astExpression, @NotNull VariableSymbol variable,
                              @Nullable String prefix) {
    super(astExpression, prefix);
    this.variable = variable;
  }

  public VariableSymbol getVariable() {
    return variable;
  }

  @Override
  public Expression copyWithPrefix(String prefix) {
    return new AssignmentExpression(astExpression, variable, prefix);
  }

  public Optional<BoolExpr> convert(Context context, IDeriveSMTExpr converter) {
    String[] prefixes = getPrefix().orElse("").split("\\.");
    String parentPrefix = Arrays.stream(prefixes).limit(prefixes.length - 1).reduce((a, b) -> a + "." + b).orElse("");
    converter.setPrefix(parentPrefix);
    Optional<Expr<?>> nameExpression =
      (new VariableArcDeriveSMTSort(new VariableArcTypeCalculator())).toSort(context, variable.getType())
        .map(s -> context.mkConst(prefix + "." + variable.getName(), s));
    Optional<Expr<?>> bindingExpression = converter.toExpr(getAstExpression());

    if (nameExpression.isPresent() && bindingExpression.isPresent()) {
      return Optional.of(context.mkEq(nameExpression.get(), bindingExpression.get()));
    }

    return Optional.empty();
  }

  @Override
  public String print() {
    return variable.getName() + " = " + VariableArcMill.prettyPrint(astExpression, false);
  }
}
