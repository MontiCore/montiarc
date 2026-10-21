/* (c) https://github.com/MontiCore/monticore */
package variablearc.smt;

import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.enumerations.Z3_decl_kind;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc.smt.exp2smt.IDeriveSMTExpr;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Decides SMT formulas of a common vocabulary in the Z3 context of the
 * presence formulas of the global scope.
 */
public class FormulaSolver {

  /**
   * Marks the name of an unknown value (see {@link IDeriveSMTExpr#setUnknown})
   * that stands for a condition whose type cannot be derived.
   */
  public static final String OBSCURE_MARKER = "?obscure";

  protected BoolExpr assumption;

  /**
   * @param assumption restricts the unknown values that {@link #check} takes
   *                   into account to those for which the assumption, e.g.,
   *                   the constraints of a component type, is satisfiable
   */
  public void assume(@NotNull BoolExpr assumption) {
    Preconditions.checkNotNull(assumption);
    this.assumption = assumption;
  }

  /**
   * @return the Z3 context of the presence formulas of the global scope
   */
  public Context getContext() {
    return VariableArcMill.globalScope().getPresenceContext()
      .orElseThrow(() -> new IllegalStateException("The global scope provides no context for presence formulas"));
  }

  /**
   * @return whether the formula contains a condition whose type cannot be
   * derived
   */
  public boolean isObscure(@NotNull BoolExpr formula) {
    Preconditions.checkNotNull(formula);
    return this.constantsOf(formula).stream()
      .anyMatch(constant -> constant.getFuncDecl().getName().toString().contains(OBSCURE_MARKER));
  }

  public BoolExpr top() {
    return this.getContext().mkTrue();
  }

  public BoolExpr bottom() {
    return this.getContext().mkFalse();
  }

  public BoolExpr and(@NotNull BoolExpr... operands) {
    return this.and(Arrays.asList(operands));
  }

  public BoolExpr and(@NotNull Collection<? extends BoolExpr> operands) {
    Preconditions.checkNotNull(operands);
    List<BoolExpr> flat = new ArrayList<>();
    for (BoolExpr operand : operands) {
      Preconditions.checkNotNull(operand);
      if (operand.isFalse()) return this.bottom();
      if (operand.isAnd()) {
        Arrays.stream(operand.getArgs()).map(BoolExpr.class::cast).filter(o -> !flat.contains(o)).forEach(flat::add);
      } else if (!operand.isTrue() && !flat.contains(operand)) {
        flat.add(operand);
      }
    }
    if (flat.isEmpty()) return this.top();
    if (flat.size() == 1) return flat.get(0);
    return this.getContext().mkAnd(flat.toArray(BoolExpr[]::new));
  }

  public BoolExpr or(@NotNull BoolExpr... operands) {
    return this.or(Arrays.asList(operands));
  }

  public BoolExpr or(@NotNull Collection<? extends BoolExpr> operands) {
    Preconditions.checkNotNull(operands);
    List<BoolExpr> flat = new ArrayList<>();
    for (BoolExpr operand : operands) {
      Preconditions.checkNotNull(operand);
      if (operand.isTrue()) return this.top();
      if (operand.isOr()) {
        Arrays.stream(operand.getArgs()).map(BoolExpr.class::cast).filter(o -> !flat.contains(o)).forEach(flat::add);
      } else if (!operand.isFalse() && !flat.contains(operand)) {
        flat.add(operand);
      }
    }
    if (flat.isEmpty()) return this.bottom();
    if (flat.size() == 1) return flat.get(0);
    return this.getContext().mkOr(flat.toArray(BoolExpr[]::new));
  }

  public BoolExpr not(@NotNull BoolExpr operand) {
    Preconditions.checkNotNull(operand);
    if (operand.isTrue()) return this.bottom();
    if (operand.isFalse()) return this.top();
    if (operand.isNot()) return (BoolExpr) operand.getArgs()[0];
    return this.getContext().mkNot(operand);
  }

  public BoolExpr implies(@NotNull BoolExpr premise, @NotNull BoolExpr conclusion) {
    return this.or(this.not(premise), conclusion);
  }

  /**
   * Checks whether the formula, e.g., the condition under which a context
   * condition is violated, is satisfiable. Expressions that cannot be
   * converted are unknown values (see {@link IDeriveSMTExpr#setUnknown}).
   *
   * @return {@link Satisfiability#SATISFIABLE} if the formula is satisfiable
   * whatever the unknown values satisfying the assumption are,
   * {@link Satisfiability#UNSATISFIABLE} if it
   * is not satisfiable, and {@link Satisfiability#UNKNOWN} if it depends on the
   * unknown values or the solver cannot decide it
   */
  public Satisfiability check(@NotNull BoolExpr formula) {
    Preconditions.checkNotNull(formula);
    Solver solver = this.getContext().mkSolver();
    solver.add(formula);
    Status status = solver.check();
    if (status == Status.UNSATISFIABLE) {
      return Satisfiability.UNSATISFIABLE;
    } else if (status == Status.UNKNOWN) {
      return Satisfiability.UNKNOWN;
    } else if (this.constantsOf(formula).stream().noneMatch(IDeriveSMTExpr::isUnknown)) {
      return Satisfiability.SATISFIABLE;
    }
    return this.isSatisfiableForAllUnknownValues(formula) ? Satisfiability.SATISFIABLE : Satisfiability.UNKNOWN;
  }

  /**
   * Checks whether the formula is satisfiable with the boolean variable set
   * to true.
   *
   * @param variable the name of a boolean variable of the formula's
   *                 vocabulary
   * @see #check(BoolExpr)
   */
  public Satisfiability checkWith(@NotNull BoolExpr formula, @NotNull String variable) {
    Preconditions.checkNotNull(formula);
    Preconditions.checkNotNull(variable);
    return this.check(this.getContext().mkAnd(formula, this.getContext().mkBoolConst(variable)));
  }

  /**
   * Checks whether the formula admits two solutions that agree on all
   * variables but the given boolean ones and differ in {@code variable},
   * i.e., whether the other variables leave {@code variable} undetermined.
   *
   * @param free     the names of boolean variables that may differ between
   *                 both solutions
   * @param variable the name of one of the free variables
   * @return the satisfiability of the existence of such solutions, see
   * {@link #check(BoolExpr)}
   */
  public Satisfiability checkUndetermined(@NotNull BoolExpr formula,
                                          @NotNull Collection<String> free,
                                          @NotNull String variable) {
    Preconditions.checkNotNull(formula);
    Preconditions.checkNotNull(free);
    Preconditions.checkNotNull(variable);
    Preconditions.checkArgument(free.contains(variable));
    BoolExpr[] originals = free.stream().map(this.getContext()::mkBoolConst).toArray(BoolExpr[]::new);
    BoolExpr[] copies = free.stream().map(name -> this.getContext().mkBoolConst(name + "'")).toArray(BoolExpr[]::new);
    BoolExpr copy = (BoolExpr) formula.substitute(originals, copies);
    BoolExpr differ = this.getContext().mkNot(this.getContext().mkEq(
      this.getContext().mkBoolConst(variable), this.getContext().mkBoolConst(variable + "'")));
    return this.check(this.getContext().mkAnd(formula, copy, differ));
  }

  /**
   * Checks whether the premise admits a solution for which no values of the
   * hidden variables satisfy the conclusion, i.e., whether the premise does
   * not imply that the conclusion is satisfiable.
   *
   * @param hidden decides by their names which variables of the conclusion are
   *               existentially quantified; unknown values never are
   * @return the satisfiability of the existence of such a solution, see
   * {@link #check(BoolExpr)}
   */
  public Satisfiability checkNotImplied(@NotNull BoolExpr premise,
                                        @NotNull BoolExpr conclusion,
                                        @NotNull Predicate<String> hidden) {
    Preconditions.checkNotNull(premise);
    Preconditions.checkNotNull(conclusion);
    Preconditions.checkNotNull(hidden);
    Expr<?>[] bound = this.constantsOf(conclusion).stream()
      .filter(constant -> !IDeriveSMTExpr.isUnknown(constant))
      .filter(constant -> hidden.test(constant.getFuncDecl().getName().toString()))
      .toArray(Expr<?>[]::new);
    BoolExpr satisfiable = bound.length == 0 ? conclusion
      : this.getContext().mkExists(bound, conclusion, 1, null, null, null, null);
    return this.check(this.getContext().mkAnd(premise, this.getContext().mkNot(satisfiable)));
  }

  /**
   * @return the uninterpreted constants of the expression
   */
  public Set<Expr<?>> constantsOf(@NotNull Expr<?> expression) {
    Set<Expr<?>> constants = new LinkedHashSet<>();
    Set<Expr<?>> visited = new HashSet<>();
    Deque<Expr<?>> worklist = new ArrayDeque<>();
    worklist.push(expression);
    while (!worklist.isEmpty()) {
      Expr<?> current = worklist.pop();
      if (!visited.add(current)) continue;
      if (current instanceof Quantifier quantifier) {
        worklist.push(quantifier.getBody());
      }
      if (!current.isApp()) continue;
      if (current.isConst() && current.getFuncDecl().getDeclKind() == Z3_decl_kind.Z3_OP_UNINTERPRETED) {
        constants.add(current);
      }
      for (Expr<?> argument : current.getArgs()) {
        worklist.push(argument);
      }
    }
    return constants;
  }


  /**
   * Checks whether the formula is satisfiable whatever the values of
   * expressions that cannot be determined statically are (see
   * {@link IDeriveSMTExpr#setUnknown}) as long as they satisfy the
   * assumption, i.e., whether
   * {@code ∀ unknowns: (∃ others: assumption) → (∃ others: formula)} holds.
   * If the solver cannot decide this, the formula is assumed to not be
   * satisfiable for all unknown values.
   */
  protected boolean isSatisfiableForAllUnknownValues(@NotNull BoolExpr formula) {
    Set<Expr<?>> constants = this.constantsOf(formula);
    if (constants.stream().noneMatch(IDeriveSMTExpr::isUnknown)) {
      return this.status(formula) == Status.SATISFIABLE;
    }
    BoolExpr noneSatisfies = this.quantifyOthers(formula, this.getContext().mkNot(formula), true);
    BoolExpr assumption = this.assumption == null ? this.getContext().mkTrue() : this.assumption;
    BoolExpr assumable = this.quantifyOthers(assumption, assumption, false);
    return this.status(this.getContext().mkAnd(assumable, noneSatisfies)) == Status.UNSATISFIABLE;
  }

  /**
   * @return the body with the constants of the formula that are not unknown
   * values quantified universally or existentially
   */
  protected BoolExpr quantifyOthers(@NotNull BoolExpr formula, @NotNull BoolExpr body, boolean universally) {
    Expr<?>[] others = this.constantsOf(formula).stream()
      .filter(c -> !IDeriveSMTExpr.isUnknown(c))
      .toArray(Expr<?>[]::new);
    if (others.length == 0) {
      return body;
    }
    return universally ? this.getContext().mkForall(others, body, 1, null, null, null, null)
      : this.getContext().mkExists(others, body, 1, null, null, null, null);
  }

  protected Status status(@NotNull BoolExpr formula) {
    Solver solver = this.getContext().mkSolver();
    solver.add(formula);
    return solver.check();
  }

  public enum Satisfiability { SATISFIABLE, UNSATISFIABLE, UNKNOWN }
}
