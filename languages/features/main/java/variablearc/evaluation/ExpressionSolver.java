/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._ast.ASTArcField;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.microsoft.z3.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc._visitor.VariableArcTraverser;
import variablearc.check.TypeExprOfVariableComponent;
import variablearc.check.VariableArcTypeCalculator;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class ExpressionSolver {

  /**
   * Solves an {@code ASTExpression} by converting it to SMT and solving it with Z3.
   *
   * @param expression         the boolean expression that is solved.
   * @param compTypeExpression the context of the expression.
   * @return a {@code Optional<Boolean>} telling us if {@code expression} is solvable or {@code Optional.empty()} if
   * the expression cannot be solved or converted by this solver.
   */
  public static Optional<Boolean> solve(@NotNull ASTExpression expression,
                                        @NotNull TypeExprOfVariableComponent compTypeExpression) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(compTypeExpression);
    return solve(Collections.singletonList(expression), compTypeExpression);
  }

  /**
   * Solves a {@code List<ASTExpression>} by converting all to SMT and solving the conjunction with Z3.
   *
   * @param expressions        the list of boolean expression that is solved.
   * @param compTypeExpression the context of the expressions.
   * @return a {@code Optional<Boolean>} telling us if {@code expressions} are solvable or {@code Optional.empty()} if
   * at least one expression cannot be solved or converted by this solver.
   */
  public static Optional<Boolean> solve(@NotNull List<ASTExpression> expressions,
                                        @NotNull TypeExprOfVariableComponent compTypeExpression) {
    Preconditions.checkNotNull(expressions);
    Preconditions.checkNotNull(compTypeExpression);

    Context ctx = new Context();
    IDeriveSMTExpr converter = VariableArcMill.fullConverter(ctx);
    ArrayList<BoolExpr> contextExpr = new ArrayList<>();

    // Convert fields
    for (VariableSymbol variable : compTypeExpression.getTypeInfo().getFields()) {
      if (variable.isPresentAstNode() && variable.getAstNode() instanceof ASTArcField) {
        Optional<Expr<?>> bindingSolverExpression = converter.toExpr(
          ((ASTArcField) variable.getAstNode()).getInitial());
        Optional<Expr<?>> nameExpression =
          (new VariableArcDeriveSMTSort(new VariableArcTypeCalculator())).toSort(ctx, variable.getType())
            .map(s -> ctx.mkConst(variable.getName(), s));
        if (bindingSolverExpression.isPresent() &&
          nameExpression.isPresent()) {
          contextExpr.add(ctx.mkEq(nameExpression.get(), bindingSolverExpression.get()));
        }
      }
    }
    // Convert parameters
    for (VariableSymbol variable : compTypeExpression.getTypeInfo().getSpannedScope().getLocalVariableSymbols()) {
      Optional<ASTExpression> bindingExpression = compTypeExpression.getBindingFor(variable);
      Optional<Expr<?>> bindingSolverExpression = bindingExpression.flatMap(converter::toExpr);
      Optional<Expr<?>> nameExpression =
        (new VariableArcDeriveSMTSort(new VariableArcTypeCalculator())).toSort(ctx, variable.getType())
          .map(s -> ctx.mkConst(variable.getName(), s));
      if (bindingExpression.isPresent() && bindingSolverExpression.isPresent() &&
        containsNoVariableOutsideScope(bindingExpression.get()) &&
        nameExpression.isPresent()) {
        contextExpr.add(ctx.mkEq(nameExpression.get(), bindingSolverExpression.get()));
      }
    }
    // Convert features
    for (ArcFeatureSymbol feature : ((IVariableArcScope) compTypeExpression.getTypeInfo()
      .getSpannedScope()).getLocalArcFeatureSymbols()) {
      Optional<ASTExpression> bindingExpression = compTypeExpression.getBindingFor(feature);
      if (bindingExpression.isPresent()) {
        Optional<Expr<?>> solverExpression = bindingExpression.flatMap(converter::toExpr);
        if (solverExpression.isPresent() && solverExpression.get().isBool()) {
          contextExpr.add(ctx.mkEq(ctx.mkBoolConst(feature.getName()), solverExpression.get()));
        }
      } else {
        // default to false for unassigned features
        contextExpr.add(ctx.mkEq(ctx.mkBoolConst(feature.getName()), ctx.mkBool(false)));
      }
    }


    BoolExpr[] smtExpr = expressions.stream().map(converter::toBool).filter(Optional::isPresent)
      .map(Optional::get)
      .toArray(BoolExpr[]::new);

    if (smtExpr.length < expressions.size()) {
      ctx.close();
      return Optional.empty();
    }

    Solver solver = ctx.mkSolver();
    solver.add(contextExpr.toArray(BoolExpr[]::new));
    solver.add(smtExpr);
    Status status = solver.check();
    ctx.close();

    switch (status) {
      case UNKNOWN:
        return Optional.empty();
      case SATISFIABLE:
        return Optional.of(true);
      case UNSATISFIABLE:
        return Optional.of(false);
    }

    return Optional.empty();
  }

  /**
   * Solves an {@code ASTExpression} by converting it to SMT and solving it with Z3.
   *
   * @param expression          the boolean expression that is solved.
   * @param componentTypeSymbol the context of the expression.
   * @return a {@code Optional<Boolean>} telling us if {@code expression} is solvable or {@code Optional.empty()} if
   * the expression cannot be solved or converted by this solver.
   */
  public static Optional<Boolean> solve(@NotNull ASTExpression expression,
                                        @NotNull ComponentTypeSymbol componentTypeSymbol) {
    Preconditions.checkNotNull(expression);
    Preconditions.checkNotNull(componentTypeSymbol);
    return solve(Collections.singletonList(expression), componentTypeSymbol);
  }

  /**
   * Solves a {@code List<ASTExpression>} by converting all to SMT and solving the conjunction with Z3.
   *
   * @param expressions         the list of boolean expression that is solved.
   * @param componentTypeSymbol the context of the expressions.
   * @return a {@code Optional<Boolean>} telling us if {@code expressions} are solvable or {@code Optional.empty()} if
   * at least one expression cannot be solved or converted by this solver.
   */
  public static Optional<Boolean> solve(@NotNull List<ASTExpression> expressions,
                                        @NotNull ComponentTypeSymbol componentTypeSymbol) {
    Preconditions.checkNotNull(expressions);
    Preconditions.checkNotNull(componentTypeSymbol);
    Context ctx = new Context();
    IDeriveSMTExpr converter = VariableArcMill.fullConverter(ctx);
    ArrayList<BoolExpr> contextExpr = new ArrayList<>();

    // Convert fields
    for (VariableSymbol variable : componentTypeSymbol.getFields()) {
      if (variable.isPresentAstNode() && variable.getAstNode() instanceof ASTArcField) {
        Optional<Expr<?>> bindingSolverExpression = converter.toExpr(
          ((ASTArcField) variable.getAstNode()).getInitial());
        Optional<Expr<?>> nameExpression =
          (new VariableArcDeriveSMTSort(new VariableArcTypeCalculator())).toSort(ctx, variable.getType())
            .map(s -> ctx.mkConst(variable.getName(), s));
        if (bindingSolverExpression.isPresent() &&
          nameExpression.isPresent()) {
          contextExpr.add(ctx.mkEq(nameExpression.get(), bindingSolverExpression.get()));
        }
      }
    }

    BoolExpr[] smtExpr = expressions.stream()
      .map(converter::toBool)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .toArray(BoolExpr[]::new);

    if (smtExpr.length < expressions.size()) {
      ctx.close();
      return Optional.empty();
    }

    Solver solver = ctx.mkSolver();
    solver.add(contextExpr.toArray(BoolExpr[]::new));
    solver.add(smtExpr);
    Status status = solver.check();
    ctx.close();

    switch (status) {
      case UNKNOWN:
        return Optional.empty();
      case SATISFIABLE:
        return Optional.of(true);
      case UNSATISFIABLE:
        return Optional.of(false);
    }

    return Optional.empty();
  }

  /*
   * Method for detecting name expressions in ASTExpressions
   */
  protected static boolean containsNoVariableOutsideScope(ASTExpression expression) {
    VariableArcTraverser traverser = VariableArcMill.traverser();
    ContainsASTNameExpressionVisitor visitor = new ContainsASTNameExpressionVisitor();
    traverser.add4ExpressionsBasis(visitor);
    traverser.add4VariableArc(visitor);
    expression.accept(traverser);
    return !visitor.getResult();
  }
}
