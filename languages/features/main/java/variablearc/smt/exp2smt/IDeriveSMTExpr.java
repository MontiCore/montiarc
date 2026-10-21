/* (c) https://github.com/MontiCore/monticore */
package variablearc.smt.exp2smt;

import java.util.Optional;
import org.codehaus.commons.nullanalysis.NotNull;
import com.google.common.base.Preconditions;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Sort;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types3.TypeCheck3;
import de.monticore.visitor.ITraverser;

public interface IDeriveSMTExpr {

  /**
   * Marks the name of an SMT constant that stands for the (statically unknown) value of an
   * expression that cannot be encoded as SMT, e.g., a method call. See {@link #setUnknown}.
   */
  String UNKNOWN_MARKER = "?unknown@";

  /**
   * @return whether {@code expr} is a constant created by {@link #setUnknown}
   */
  static boolean isUnknown(@NotNull Expr<?> expr) {
    Preconditions.checkNotNull(expr);
    return expr.isConst() && expr.getFuncDecl().getName().toString().contains(UNKNOWN_MARKER);
  }

  /**
   * Sets the result to an unconstrained constant of the sort of {@code node}, i.e., the value of
   * {@code node} may be any value of its sort. Clears the result if the sort of {@code node}
   * cannot be determined, i.e., if {@code node} has no SMT sort.
   *
   * @param node an expression that cannot be encoded as SMT
   * @see #setUnknown(ASTExpression, Sort)
   */
  default void setUnknown(@NotNull ASTExpression node) {
    Preconditions.checkNotNull(node);
    Optional<Sort> sort = getSortDerive().toSort(node);
    if (sort.isPresent()) {
      setUnknown(node, sort.get());
    } else {
      getResult().clear();
    }
  }

  /**
   * Sets the result to an unconstrained constant of the given {@code sort}, i.e., the value of
   * {@code node} may be any value of its sort. The constant is named by {@link #unknownName},
   * such that the same expression always stands for the same value.
   *
   * @param node an expression that cannot be encoded as SMT
   * @param sort the sort of {@code node}
   */
  default void setUnknown(@NotNull ASTExpression node, @NotNull Sort sort) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(sort);
    getResult().setValue(getContext().mkConst(unknownName(node), sort));
  }

  /**
   * @return a name containing {@link #UNKNOWN_MARKER} that is unique to the
   * expression among all expressions this converter encodes, independent of
   * their source positions
   */
  @NotNull String unknownName(@NotNull ASTExpression node);

  @NotNull Context getContext();

  @NotNull IDeriveSMTSort getSortDerive();

  @NotNull Expr2SMTResult getResult();

  @NotNull ITraverser getTraverser();

  /**
   * @param name the (possibly qualified) name of a variable, e.g., {@code n}
   *             or {@code sub.f}
   * @return the SMT constant the variable stands for
   */
  default @NotNull Expr<?> variable(@NotNull String name, @NotNull Sort sort) {
    Preconditions.checkNotNull(name);
    Preconditions.checkNotNull(sort);
    return getContext().mkConst(name, sort);
  }

  default @NotNull Optional<Expr<?>> toExpr(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    getResult().reset();
    if (!isObscure(expr)) {
      expr.accept(getTraverser());
    }
    return getResult().getValue();
  }

  default @NotNull Optional<IntExpr> toInt(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    getResult().reset();
    if (!isObscure(expr)) {
      expr.accept(getTraverser());
    }
    return getResult().getValueAsInt();
  }

  default @NotNull Optional<BoolExpr> toBool(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    getResult().reset();
    if (!isObscure(expr)) {
      expr.accept(getTraverser());
    }
    return getResult().getValueAsBool();
  }

  default @NotNull Optional<ArithExpr<?>> toArith(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    getResult().reset();
    if (!isObscure(expr)) {
      expr.accept(getTraverser());
    }
    return getResult().getValueAsArith();
  }

  /**
   * @return whether the type of {@code expr} cannot be derived, i.e., whether {@code expr} is
   * invalid and its error is reported elsewhere
   */
  static boolean isObscure(@NotNull ASTExpression expr) {
    Preconditions.checkNotNull(expr);
    return expr.getEnclosingScope() != null && TypeCheck3.typeOf(expr).isObscureType();
  }
}
