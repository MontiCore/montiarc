/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import java.util.Optional;
import org.codehaus.commons.nullanalysis.NotNull;
import com.google.common.base.Preconditions;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

public class Expr2SMTResult {

  protected Expr<?> value;
  protected ASTExpression failureCause;
  protected String failureReason;

  public void clear() {
    this.value = null;
  }

  public void reset() {
    this.value = null;
    this.failureCause = null;
    this.failureReason = null;
  }

  public void markFailure(@NotNull ASTExpression cause, @NotNull String reason) {
    Preconditions.checkNotNull(cause);
    Preconditions.checkNotNull(reason);
    if (this.failureCause == null) {
      this.failureCause = cause;
      this.failureReason = reason;
    }
  }

  public Optional<ASTExpression> getFailureCause() {
    return Optional.ofNullable(this.failureCause);
  }

  public Optional<String> getFailureReason() {
    return Optional.ofNullable(this.failureReason);
  }

  public Optional<Expr<?>> getValue() {
    return Optional.ofNullable(this.value);
  }

  public void setValue(@NotNull Expr<?> value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }

  public Optional<ArithExpr<?>> getValueAsArith() {
    if (isArith()) {
      return Optional.of((ArithExpr<?>) this.value);
    }
    return Optional.empty();
  }

  public boolean isArith() {
    return this.value != null && this.value instanceof ArithExpr<?>;
  }

  public Optional<BoolExpr> getValueAsBool() {
    if (isBool()) {
      return Optional.of((BoolExpr)this.value);
    }
    return Optional.empty();
  }

  public boolean isBool() {
    return this.value != null && this.value instanceof BoolExpr;
  }

  public Optional<IntExpr> getValueAsInt() {
    if (isInt()) {
      return Optional.of((IntExpr) this.value);
    }
    return Optional.empty();
  }

  public boolean isInt() {
    return this.value != null && this.value instanceof IntExpr;
  }
}
