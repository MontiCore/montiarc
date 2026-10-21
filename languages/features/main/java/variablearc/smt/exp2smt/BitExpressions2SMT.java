/* (c) https://github.com/MontiCore/monticore */
package variablearc.smt.exp2smt;

import com.google.common.base.Preconditions;
import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryAndExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryOrOpExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryXorExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsHandler;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types3.TypeCheck3;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;
import java.util.function.BinaryOperator;

public class BitExpressions2SMT implements BitExpressionsHandler {

  protected final IDeriveSMTExpr deriveSMTExpr;
  protected BitExpressionsTraverser traverser;

  protected Context getContext() {
    return this.deriveSMTExpr.getContext();
  }

  public BitExpressions2SMT(@NotNull IDeriveSMTExpr deriveSMTExpr) {
    Preconditions.checkNotNull(deriveSMTExpr);
    this.deriveSMTExpr = deriveSMTExpr;
  }

  @Override
  public void setTraverser(@NotNull BitExpressionsTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public BitExpressionsTraverser getTraverser() {
    return traverser;
  }

  protected Expr2SMTResult getResult() {
    return this.deriveSMTExpr.getResult();
  }

  /**
   * @return the shifted value with the width of the type of the expression,
   * i.e., 32 bits unless it is a long, and the distance masked to that width,
   * as in Java
   */
  protected IntExpr shift(@NotNull ASTExpression node,
                          @NotNull IntExpr value,
                          @NotNull IntExpr distance,
                          @NotNull BinaryOperator<BitVecExpr> operator) {
    int width = TypeCheck3.typeOf(node).printFullName().equals(BasicSymbolsMill.LONG) ? 64 : 32;
    BitVecExpr masked = this.getContext().mkBVAND(this.getContext().mkInt2BV(width, distance),
      this.getContext().mkBV(width - 1, width));
    return this.getContext().mkBV2Int(operator.apply(this.getContext().mkInt2BV(width, value), masked), true);
  }

  @Override
  public void handle(@NotNull ASTLeftShiftExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<IntExpr> left = this.getResult().getValueAsInt();

    node.getRight().accept(getTraverser());
    Optional<IntExpr> right = this.getResult().getValueAsInt();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.shift(node, left.get(), right.get(), this.getContext()::mkBVSHL));
    } else {
      this.deriveSMTExpr.setUnknown(node);
    }
  }

  @Override
  public void handle(@NotNull ASTRightShiftExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<IntExpr> left = this.getResult().getValueAsInt();

    node.getRight().accept(getTraverser());
    Optional<IntExpr> right = this.getResult().getValueAsInt();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.shift(node, left.get(), right.get(), this.getContext()::mkBVASHR));
    } else {
      this.deriveSMTExpr.setUnknown(node);
    }
  }

  @Override
  public void handle(@NotNull ASTLogicalRightShiftExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<IntExpr> left = this.getResult().getValueAsInt();

    node.getRight().accept(getTraverser());
    Optional<IntExpr> right = this.getResult().getValueAsInt();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.shift(node, left.get(), right.get(), this.getContext()::mkBVLSHR));;
    } else {
      this.deriveSMTExpr.setUnknown(node);
    }
  }

  @Override
  public void handle(@NotNull ASTBinaryAndExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<BoolExpr> leftBool = this.getResult().getValueAsBool();
    Optional<IntExpr> leftInt = this.getResult().getValueAsInt();

    node.getRight().accept(getTraverser());
    Optional<BoolExpr> rightBool = this.getResult().getValueAsBool();
    Optional<IntExpr> rightInt = this.getResult().getValueAsInt();

    if (leftBool.isPresent() && rightBool.isPresent()) {
      this.getResult().setValue(this.getContext().mkAnd(leftBool.get(), rightBool.get()));
    } else if (leftInt.isPresent() && rightInt.isPresent()) {
      this.getResult().setValue(this.getContext().mkBV2Int(
        this.getContext().mkBVAND(
          this.getContext().mkInt2BV(64, leftInt.get()),
          this.getContext().mkInt2BV(64, rightInt.get())
        ), true)
      );
    } else {
      this.deriveSMTExpr.setUnknown(node);
    }
  }

  @Override
  public void handle(@NotNull ASTBinaryOrOpExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<BoolExpr> leftBool = this.getResult().getValueAsBool();
    Optional<IntExpr> leftInt = this.getResult().getValueAsInt();

    node.getRight().accept(getTraverser());
    Optional<BoolExpr> rightBool = this.getResult().getValueAsBool();
    Optional<IntExpr> rightInt = this.getResult().getValueAsInt();

    if (leftBool.isPresent() && rightBool.isPresent()) {
      this.getResult().setValue(this.getContext().mkOr(leftBool.get(), rightBool.get()));
    } else if (leftInt.isPresent() && rightInt.isPresent()) {
      this.getResult().setValue(this.getContext().mkBV2Int(
        this.getContext().mkBVOR(
          this.getContext().mkInt2BV(64, leftInt.get()),
          this.getContext().mkInt2BV(64, rightInt.get())
        ), true)
      );
    } else {
      this.deriveSMTExpr.setUnknown(node);
    }
  }

  @Override
  public void handle(@NotNull ASTBinaryXorExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<BoolExpr> leftBool = this.getResult().getValueAsBool();
    Optional<IntExpr> leftInt = this.getResult().getValueAsInt();

    node.getRight().accept(getTraverser());
    Optional<BoolExpr> rightBool = this.getResult().getValueAsBool();
    Optional<IntExpr> rightInt = this.getResult().getValueAsInt();

    if (leftBool.isPresent() && rightBool.isPresent()) {
      this.getResult().setValue(this.getContext().mkXor(leftBool.get(), rightBool.get()));
    } else if (leftInt.isPresent() && rightInt.isPresent()) {
      this.getResult().setValue(this.getContext().mkBV2Int(
        this.getContext().mkBVXOR(
          this.getContext().mkInt2BV(64, leftInt.get()),
          this.getContext().mkInt2BV(64, rightInt.get())
        ), true)
      );
    } else {
      this.deriveSMTExpr.setUnknown(node);
    }
  }
}
