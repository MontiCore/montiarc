/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import com.google.common.base.Preconditions;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryAndExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryOrOpExpression;
import de.monticore.expressions.bitexpressions._ast.ASTBinaryXorExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLeftShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTLogicalRightShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTRightShiftExpression;
import de.monticore.expressions.bitexpressions._ast.ASTShiftExpression;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsHandler;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

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

  @Override
  public void handle(@NotNull ASTLeftShiftExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<IntExpr> left = this.getResult().getValueAsInt();

    node.getRight().accept(getTraverser());
    Optional<IntExpr> right = this.getResult().getValueAsInt();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkBV2Int(this.getContext().mkBVSHL(this.getContext().mkInt2BV(64, left.get()), this.getContext().mkInt2BV(64, right.get())), true));
    } else {
      this.getResult().clear();
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
      this.getResult().setValue(this.getContext().mkBV2Int(this.getContext().mkBVASHR(this.getContext().mkInt2BV(64, left.get()), this.getContext().mkInt2BV(64, right.get())), true));
    } else {
      this.getResult().clear();
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
      this.getResult().setValue(this.getContext().mkBV2Int(this.getContext().mkBVLSHR(this.getContext().mkInt2BV(64, left.get()), this.getContext().mkInt2BV(64, right.get())), true));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTBinaryAndExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<IntExpr> left = this.getResult().getValueAsInt();

    node.getRight().accept(getTraverser());
    Optional<IntExpr> right = this.getResult().getValueAsInt();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkBV2Int(this.getContext().mkBVAND(this.getContext().mkInt2BV(64, left.get()), this.getContext().mkInt2BV(64, right.get())), true));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTBinaryXorExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<IntExpr> left = this.getResult().getValueAsInt();

    node.getRight().accept(getTraverser());
    Optional<IntExpr> right = this.getResult().getValueAsInt();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkBV2Int(this.getContext().mkBVXOR(this.getContext().mkInt2BV(64, left.get()), this.getContext().mkInt2BV(64, right.get())), true));
    } else {
      this.getResult().clear();
    }
  }

  @Override
  public void handle(@NotNull ASTBinaryOrOpExpression node) {
    Preconditions.checkNotNull(node);
    node.getLeft().accept(getTraverser());
    Optional<IntExpr> left = this.getResult().getValueAsInt();

    node.getRight().accept(getTraverser());
    Optional<IntExpr> right = this.getResult().getValueAsInt();

    if (left.isPresent() && right.isPresent()) {
      this.getResult().setValue(this.getContext().mkBV2Int(this.getContext().mkBVOR(this.getContext().mkInt2BV(64, left.get()), this.getContext().mkInt2BV(64, right.get())), true));
    } else {
      this.getResult().clear();
    }
  }
}
