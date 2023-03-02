/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import com.microsoft.z3.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;

import java.util.*;

public class ExpressionSolver {

  protected Boolean lastResult;
  protected Context context;
  protected BoolExpr[] defaultExpr;
  protected String defaultPrefix;

  public Optional<Boolean> getLastResult() {
    return Optional.ofNullable(lastResult);
  }

  /**
   * @param componentInstanceSymbol the instance symbol that holds the expressions.
   */

  public ExpressionSolver(@NotNull ComponentInstanceSymbol componentInstanceSymbol) {
    Preconditions.checkNotNull(componentInstanceSymbol);

    this.context = new Context();
    ComponentConverter componentConverter = new ComponentConverter(context);
    this.defaultExpr = componentConverter.convert(componentInstanceSymbol).toArray(BoolExpr[]::new);
    this.defaultPrefix = componentInstanceSymbol.getType().printName();
  }

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
   * Solves an {@code ASTExpression} by converting it to SMT and solving it with Z3.
   *
   * @param expression the boolean expression that is solved.
   * @return a {@code Optional<Boolean>} telling us if {@code expression} is solvable or {@code Optional.empty()} if
   * the expression cannot be solved or converted by this solver.
   */
  public Optional<Boolean> solve(@NotNull ASTExpression expression) {
    Preconditions.checkNotNull(expression);
    return solve(Collections.singletonList(expression));
  }

  /**
   * Solves a {@code List<ASTExpression>} by converting all to SMT and solving the conjunction with Z3.
   *
   * @param expressions the list of boolean expression that is solved.
   * @return a {@code Optional<Boolean>} telling us if {@code expressions} are solvable or {@code Optional.empty()} if
   * at least one expression cannot be solved or converted by this solver.
   */
  public Optional<Boolean> solve(@NotNull List<ASTExpression> expressions) {
    Preconditions.checkNotNull(expressions);

    IDeriveSMTExpr converter = VariableArcMill.fullConverter(context);
    converter.setPrefix(defaultPrefix);
    BoolExpr[] smtExpr = expressions.stream()
      .map(converter::toBool)
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

  /**
   * Disposes of the solver
   */
  public void close() {
    context.close();
  }
}
