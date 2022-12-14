/*
  (c) https://github.com/MontiCore/monticore
 */

package montiarc.evaluation;

import com.google.common.base.Preconditions;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import montiarc.MontiArcMill;
import montiarc._visitor.MontiArcTraverser;
import montiarc.check.MontiArcTypeCalculator;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc.evaluation.exp2smt.Expr2SMTResult;
import variablearc.evaluation.exp2smt.IDeriveSMTExpr;
import variablearc.evaluation.exp2smt.BitExpressions2SMT;
import variablearc.evaluation.exp2smt.CommonExpressions2SMT;
import variablearc.evaluation.exp2smt.ExpressionsBasis2SMT;
import variablearc.evaluation.exp2smt.MCCommonLiterals2SMT;
import variablearc.evaluation.VariableArcDeriveSMTSort;

import java.util.Optional;

public final class MontiArcDeriveSMTExpr implements IDeriveSMTExpr {

  private final MontiArcTraverser traverser;
  private final Expr2SMTResult result;

  public MontiArcDeriveSMTExpr(@NotNull Context context) {
    Preconditions.checkNotNull(context);
    this.result = new Expr2SMTResult();
    this.traverser = MontiArcMill.traverser();

    VariableArcDeriveSMTSort expr2Sort = new VariableArcDeriveSMTSort(new MontiArcTypeCalculator());
    this.traverser.setExpressionsBasisHandler(new ExpressionsBasis2SMT(this.result, context, expr2Sort));
    this.traverser.setMCCommonLiteralsHandler(new MCCommonLiterals2SMT(this.result, context));
    this.traverser.setCommonExpressionsHandler(new CommonExpressions2SMT(this.result, context, expr2Sort));
    this.traverser.setBitExpressionsHandler(new BitExpressions2SMT(this.result));
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
