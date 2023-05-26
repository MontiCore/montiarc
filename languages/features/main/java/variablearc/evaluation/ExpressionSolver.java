/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;
import variablearc.evaluation.expressions.Expression;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Used for solving expressions in a component.
 */
public class ExpressionSolver {

  protected Context context;

  /**
   *
   */
  public ExpressionSolver() {
    this.context = new Context();
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
          .map(e -> e.convert(context, converter)),
        expressions.getNegatedConjunctions().stream()
          .map(l -> l
            .stream()
            .map(e -> e.convert(context, converter))
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
    solver.add(smtExpr);
    Status status = solver.check();

    switch (status) {
      case SATISFIABLE:
        return Optional.of(true);
      case UNSATISFIABLE:
        return Optional.of(false);
      default:
      case UNKNOWN:
        return Optional.empty();
    }
  }

  /**
   * Disposes of the solver
   */
  public void close() {
    context.close();
  }
}
