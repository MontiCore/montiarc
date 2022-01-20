/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import variablearc.VariableArcMill;
import variablearc._symboltable.ArcFeatureSymbol;
import variablearc._symboltable.IVariableArcScope;
import variablearc._visitor.VariableArcTraverser;
import variablearc.check.TypeExprOfVariableComponent;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public abstract class ExpressionSolver {
  /**
   * Solves {@code ASTExpression} by converting them into javascript code.
   *
   * @param expression the {@code Boolean} expression that is solved.
   * @param compTypeExpression the context of the expression
   * @return a {@code Optional<Boolean>} which represent the value of the solved {@code expression} or {@code Optional.empty()} if
   * the expression cannot be solved by this solver.
   */
  public static Optional<Boolean> solve(ASTExpression expression, TypeExprOfVariableComponent compTypeExpression, Function<ASTExpression, String> print) {
    try {
      ScriptEngineManager mgr = new ScriptEngineManager();
      ScriptEngine engine = mgr.getEngineByName("JavaScript");
      StringBuilder s = new StringBuilder();
      for (VariableSymbol variable : compTypeExpression.getTypeInfo().getSpannedScope().getLocalVariableSymbols()) {
        Optional<ASTExpression> variableExpression = compTypeExpression.getBindingFor(variable);
        if (variableExpression.isPresent() && containsNoVariableOutsideScope(variableExpression.get())) {
          s.append(variable.getName()).append("=");
          s.append(print.apply(variableExpression.get())).append(";");
        }
      }
      for (ArcFeatureSymbol feature : ((IVariableArcScope) compTypeExpression.getTypeInfo().getSpannedScope()).getLocalArcFeatureSymbols()) {
        Optional<ASTExpression> variableExpression = compTypeExpression.getBindingFor(feature);
        if (variableExpression.isPresent()) {
          if (containsNoVariableOutsideScope(variableExpression.get())) {
            s.append(feature.getName()).append("=");
            s.append(print.apply(variableExpression.get())).append(";");
          }
        }
        else {
          s.append(feature.getName()).append("=false;"); // default to false for features when not bound
        }
      }
      s.append(print.apply(expression)).append(";");
      Object output = engine.eval(s.toString());
      if (output != null) {
        if (Objects.equals(output.toString(), "false")) {
          return Optional.of(false);
        }
        else if (Objects.equals(output.toString(), "true")) {
          return Optional.of(true);
        }
      }
    }
    catch (ScriptException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  protected static boolean containsNoVariableOutsideScope(ASTExpression expression) {
    VariableArcTraverser traverser = VariableArcMill.traverser();
    ContainsASTNameExpressionVisitor visitor = new ContainsASTNameExpressionVisitor();
    traverser.add4ExpressionsBasis(visitor);
    traverser.add4VariableArc(visitor);
    expression.accept(traverser);
    return !visitor.getResult();
  }
}
