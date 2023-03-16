/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import com.google.common.base.Preconditions;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.visitor.ITraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public interface IDeriveSMTExpr {

  @NotNull Context getContext();

  @NotNull IDeriveSMTSort getSortDerive();

  @NotNull Expr2SMTResult getResult();

  @NotNull ITraverser getTraverser();

  default @NotNull String getPrefix() {
    return "";
  }

  default void setPrefix(@NotNull String prefix) {}

  default @NotNull Optional<Expr<?>> toExpr(@NotNull ASTExpression expr) {
      Preconditions.checkNotNull(expr);
      getResult().clear();
      expr.accept(getTraverser());
      return getResult().getValue();
  }

  default @NotNull Optional<IntExpr> toInt(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    getResult().clear();
    expr.accept(getTraverser());
    return getResult().getValueAsInt();
  }

  default @NotNull Optional<BoolExpr> toBool(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    getResult().clear();
    expr.accept(getTraverser());
    return getResult().getValueAsBool();
  }

  default @NotNull Optional<ArithExpr<?>> toArith(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    getResult().clear();
    expr.accept(getTraverser());
    return getResult().getValueAsArith();
  }
}
