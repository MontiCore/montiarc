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

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Used for solving expressions in a component.
 */
public class ExpressionSolver {

  protected Context context;

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

    Status status = getSolver(expressions).map(Solver::check).orElse(Status.UNKNOWN);

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
   * Gets a smt solver with the {@code expressions} already included as constraints in the SMT context of this {@code ExpressionSolver}
   *
   * @param expressions the list of boolean expression that are added as constraints to the solver.
   * @return The smt solver
   */
  public Optional<Solver> getSolver(@NotNull ExpressionSet expressions) {
    Preconditions.checkNotNull(expressions);

    Optional<BoolExpr[]> smtExprOpt = convert(expressions);
    if (smtExprOpt.isEmpty()) return Optional.empty();
    BoolExpr[] smtExpr = smtExprOpt.get();

    Solver solver = context.mkSolver();
    solver.add(smtExpr);
    return Optional.of(solver);
  }

  /**
   * Converts an expression set to smt formulas in the SMT context of this {@code ExpressionSolver}
   *
   * @param expressions which are converted
   * @return An array of BoolExpr or empty if not all expression could be converted
   */
  public Optional<BoolExpr[]> convert(@NotNull ExpressionSet expressions) {
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
    return Optional.of(smtExpr);
  }

  /**
   * @return the SMT context of this {@code ExpressionSolver}
   */
  public Context getContext() {
    return context;
  }

  /**
   * Disposes of the solver
   */
  public void close() {
    context.close();
  }
}
