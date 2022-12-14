/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation;

import com.google.common.base.Preconditions;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;
import variablearc.check.VariableArcTypeCalculator;
import variablearc.evaluation.exp2smt.Expr2SMTResult;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;
import variablearc.evaluation.exp2smt.ExpressionsBasis2SMT;
import variablearc.evaluation.exp2smt.MCCommonLiterals2SMT;

import java.util.Optional;

public final class VariableArcDeriveSMTExpr implements IDeriveSMTExpr {

  private final VariableArcTraverser traverser;
  private final Expr2SMTResult result;

  public VariableArcDeriveSMTExpr(@NotNull Context context) {
    Preconditions.checkNotNull(context);
    this.result = new Expr2SMTResult();
    this.traverser = VariableArcMill.traverser();

    VariableArcDeriveSMTSort expr2Sort = new VariableArcDeriveSMTSort(new VariableArcTypeCalculator());
    this.traverser.setExpressionsBasisHandler(new ExpressionsBasis2SMT(this.result, context, expr2Sort));
    this.traverser.setMCCommonLiteralsHandler(new MCCommonLiterals2SMT(this.result, context));
  }

  @Override
  public Optional<Expr<?>> toExpr(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    this.result.clear();
    expr.accept(this.traverser);
    return this.result.getValue();
  }

  @Override
  public Optional<IntExpr> toInt(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    this.result.clear();
    expr.accept(this.traverser);
    return this.result.getValueAsInt();
  }

  @Override
  public Optional<BoolExpr> toBool(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    this.result.clear();
    expr.accept(this.traverser);
    return this.result.getValueAsBool();
  }

  @Override
  public Optional<ArithExpr<?>> toArith(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    this.result.clear();
    expr.accept(this.traverser);
    return this.result.getValueAsArith();
  }
}
