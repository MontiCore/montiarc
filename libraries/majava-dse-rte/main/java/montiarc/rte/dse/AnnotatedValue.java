/* (c) https://github.com/MontiCore/monticore */
package montiarc.rte.dse;

import com.microsoft.z3.Expr;
import com.microsoft.z3.Sort;

public class AnnotatedValue<SMTExpr extends Expr<? extends Sort>, T> {

  private final SMTExpr expr;
  private final T value;

  public AnnotatedValue(SMTExpr expr, T value) {
    this.expr = expr;
    this.value = value;
  }

  public static <G extends Expr<? extends Sort>, T> AnnotatedValue<G, T> newAnnoValue(G expr,
                                                                                      T value) {
    return new AnnotatedValue<>(expr, value);
  }

  public SMTExpr getExpr() {
    return this.expr;
  }

  public T getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return "<" + expr.toString() + ", " + value.toString() + ">";
  }
}
