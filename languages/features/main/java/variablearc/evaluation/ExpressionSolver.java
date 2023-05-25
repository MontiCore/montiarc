/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Used for solving expressions in a component.
 */
public class ExpressionSolver {

  protected Boolean lastResult;
  protected Context context;
  protected BoolExpr[] defaultExpr;
  protected String defaultPrefix;

  /**
   * @param componentTypeSymbol the context of the expressions.
   */
  public ExpressionSolver(@NotNull ComponentTypeSymbol componentTypeSymbol) {
    Preconditions.checkNotNull(componentTypeSymbol);

    this.context = new Context();
    ComponentConverter componentConverter = new ComponentConverter(context);
    this.defaultExpr = componentConverter.convert(componentTypeSymbol).toArray(BoolExpr[]::new);
    this.defaultPrefix = "";
  }

  /**
   * Solves a {@code List<ASTExpression>} by converting all to SMT and solving the conjunction with Z3.
   * The component's constraints this solver was initialized with are always added.
   *
   * @param expressions the list of boolean expression that are solved.
   * @return a {@code Optional<Boolean>} telling us if {@code expressions} are solvable or {@code Optional.empty()} if
   * at least one expression cannot be solved or converted by this solver.
   */
  public Optional<Boolean> solve(@NotNull ExpressionSet expressions) {
    Preconditions.checkNotNull(expressions);

    IDeriveSMTExpr converter = VariableArcMill.fullConverter(context);
    BoolExpr[] smtExpr = Stream.concat(
        expressions.getExpressions().stream()
          .map(expression -> convert(expression, converter)),
        expressions.getNegatedConjunctions().stream()
          .map(l -> l
            .stream()
            .map(expression -> convert(expression, converter))
            .reduce((a, b) -> (a.isPresent() && b.isPresent()) ?
              Optional.of(context.mkAnd(a.get(), b.get())) : Optional.empty())
            .orElse(Optional.empty())
            .map(context::mkNot)
          )
      )
      .filter(Optional::isPresent)
      .map(Optional::get)
      .toArray(BoolExpr[]::new);

    if (smtExpr.length < expressions.size()) {
      return Optional.empty();
    }

    Solver solver = context.mkSolver();
    solver.add(defaultExpr);
    solver.add(smtExpr);
    Status status = solver.check();

    switch (status) {
      case UNKNOWN:
        lastResult = null;
        break;
      case SATISFIABLE:
        lastResult = true;
        break;
      case UNSATISFIABLE:
        lastResult = false;
        break;
    }


    return Optional.ofNullable(lastResult);
  }

  public Optional<BoolExpr> convert(Expression expression, IDeriveSMTExpr converter) {
    converter.setPrefix(expression.getPrefix().orElse(defaultPrefix));
    if (expression.isNegated())
      return converter.toBool(expression.getAstExpression()).map(context::mkNot);
    return converter.toBool(expression.getAstExpression());
  }

  /**
   * Disposes of the solver
   */
  public void close() {
    context.close();
  }
}
