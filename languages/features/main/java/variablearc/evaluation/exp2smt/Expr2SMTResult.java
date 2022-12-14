/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import com.google.common.base.Preconditions;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class Expr2SMTResult {

  protected Expr<?> value;

  public void clear() {
    this.value = null;
  }

  public Optional<Expr<?>> getValue() {
    return Optional.ofNullable(this.value);
  }

  public void setValue(@NotNull Expr<?> value) {
    Preconditions.checkNotNull(value);
    this.value = value;
  }

  public Optional<ArithExpr<?>> getValueAsArith() {
    if (this.value != null && this.value instanceof ArithExpr<?>) {
      return Optional.of((ArithExpr<?>) this.value);
    }
    return Optional.empty();
  }

  public Optional<BoolExpr> getValueAsBool() {
    if (this.value != null && this.value instanceof BoolExpr) {
      return Optional.of((BoolExpr)this.value);
    }
    return Optional.empty();
  }

  public Optional<IntExpr> getValueAsInt() {
    if (this.value != null && this.value instanceof IntExpr) {
      return Optional.of((IntExpr) this.value);
    }
    return Optional.empty();
  }
}
