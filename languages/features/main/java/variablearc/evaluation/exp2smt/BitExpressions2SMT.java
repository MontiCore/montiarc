/* (c) https://github.com/MontiCore/monticore */
package variablearc.evaluation.exp2smt;

import com.google.common.base.Preconditions;
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

public class BitExpressions2SMT implements BitExpressionsHandler {

  protected final IDeriveSMTExpr deriveSMTExpr;
  protected BitExpressionsTraverser traverser;

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
