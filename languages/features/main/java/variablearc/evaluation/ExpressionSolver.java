/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.enumerations.Z3_decl_kind;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Used for solving expressions in a component.
 */
public class ExpressionSolver {

  protected Context context;
  protected IDeriveSMTExpr converter;

  public ExpressionSolver() {
    this.context = new Context();
    this.converter = VariableArcMill.fullConverter(context);
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
      case UNKNOWN:
      default:
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

  /**
   * Checks whether {@code formula} is satisfiable whatever the values of expressions that cannot
   * be determined statically are (see {@link IDeriveSMTExpr#setUnknown}), i.e., whether
   * {@code ∀ unknowns ∃ others: formula} holds. Without unknown values, this is plain
   * satisfiability. If the solver cannot decide this, the formula is assumed to not be satisfiable
   * for all unknown values.
   *
   * @param solver a solver of {@code context}, which is left unchanged
   */
  public static boolean isSatisfiableForAllUnknownValues(@NotNull Context context, @NotNull Solver solver,
                                                         @NotNull BoolExpr formula) {
    Preconditions.checkNotNull(context);
    Preconditions.checkNotNull(solver);
    Preconditions.checkNotNull(formula);
    Set<Expr<?>> constants = collectConstants(formula);
    if (constants.stream().noneMatch(IDeriveSMTExpr::isUnknown)) {
      return check(solver, formula) == Status.SATISFIABLE;
    }
    Expr<?>[] others = constants.stream().filter(c -> !IDeriveSMTExpr.isUnknown(c)).toArray(Expr<?>[]::new);
    BoolExpr noneSatisfies = others.length == 0 ? context.mkNot(formula)
      : context.mkForall(others, context.mkNot(formula), 1, null, null, null, null);
    return check(solver, noneSatisfies) == Status.UNSATISFIABLE;
  }

  /**
   * Checks whether {@code formula} is unsatisfiable for some values of expressions that cannot be
   * determined statically (see {@link IDeriveSMTExpr#setUnknown}), i.e., the negation of
   * {@link #isSatisfiableForAllUnknownValues}.
   *
   * @param solver a solver of {@code context}, which is left unchanged
   */
  public static boolean isUnsatisfiableForSomeUnknownValues(@NotNull Context context, @NotNull Solver solver,
                                                            @NotNull BoolExpr formula) {
    return !isSatisfiableForAllUnknownValues(context, solver, formula);
  }

  private static Status check(@NotNull Solver solver, @NotNull BoolExpr formula) {
    solver.push();
    try {
      solver.add(formula);
      return solver.check();
    } finally {
      solver.pop();
    }
  }

  private static Set<Expr<?>> collectConstants(@NotNull Expr<?> expr) {
    Set<Expr<?>> constants = new LinkedHashSet<>();
    Set<Expr<?>> visited = new HashSet<>();
    Deque<Expr<?>> worklist = new ArrayDeque<>();
    worklist.push(expr);
    while (!worklist.isEmpty()) {
      Expr<?> current = worklist.pop();
      if (!current.isApp() || !visited.add(current)) {
        continue;
      }
      if (current.isConst() && current.getFuncDecl().getDeclKind() == Z3_decl_kind.Z3_OP_UNINTERPRETED) {
        constants.add(current);
      }
      for (Expr<?> arg : current.getArgs()) {
        worklist.push(arg);
      }
    }
    return constants;
  }
}
