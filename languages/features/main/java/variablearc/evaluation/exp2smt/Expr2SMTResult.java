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
