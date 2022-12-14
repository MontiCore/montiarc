/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import com.google.common.base.Preconditions;
import de.monticore.expressions.bitexpressions._ast.*;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsHandler;
import de.monticore.expressions.bitexpressions._visitor.BitExpressionsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

public class BitExpressions2SMT implements BitExpressionsHandler {

  Expr2SMTResult result;
  BitExpressionsTraverser traverser;

  public BitExpressions2SMT(@NotNull Expr2SMTResult result) {
    Preconditions.checkNotNull(result);
    this.result = result;
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
    return this.result;
  }

  @Override
  public void handle(@NotNull ASTLeftShiftExpression node) {
    Preconditions.checkNotNull(node);
    this.getResult().clear();
  }

  @Override
  public void handle(@NotNull ASTRightShiftExpression node) {
    Preconditions.checkNotNull(node);
    this.getResult().clear();
  }

  @Override
  public void handle(@NotNull ASTLogicalRightShiftExpression node) {
    Preconditions.checkNotNull(node);
    this.getResult().clear();
  }

  @Override
  public void handle(@NotNull ASTBinaryAndExpression node) {
    Preconditions.checkNotNull(node);
    this.getResult().clear();
  }

  @Override
  public void handle(@NotNull ASTBinaryXorExpression node) {
    Preconditions.checkNotNull(node);
    BitExpressionsHandler.super.handle(node);
  }

  @Override
  public void handle(@NotNull ASTBinaryOrOpExpression node) {
    Preconditions.checkNotNull(node);
    BitExpressionsHandler.super.handle(node);
  }

  @Override
  public void handle(@NotNull ASTShiftExpression node) {
    Preconditions.checkNotNull(node);
    BitExpressionsHandler.super.handle(node);
  }
}
