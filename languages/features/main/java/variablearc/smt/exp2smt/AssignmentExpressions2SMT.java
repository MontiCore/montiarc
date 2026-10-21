/* (c) https://github.com/MontiCore/monticore */
package variablearc.smt.exp2smt;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsHandler;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

public class AssignmentExpressions2SMT implements AssignmentExpressionsHandler {

  protected final IDeriveSMTExpr deriveSMTExpr;
  protected AssignmentExpressionsTraverser traverser;

  public AssignmentExpressions2SMT(@NotNull IDeriveSMTExpr deriveSMTExpr) {
    Preconditions.checkNotNull(deriveSMTExpr);
    this.deriveSMTExpr = deriveSMTExpr;
  }

  @Override
  public void setTraverser(@NotNull AssignmentExpressionsTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public AssignmentExpressionsTraverser getTraverser() {
    return this.traverser;
  }

  protected Expr2SMTResult getResult() {
    return this.deriveSMTExpr.getResult();
  }

  @Override
  public void handle(@NotNull ASTAssignmentExpression node) {
    Preconditions.checkNotNull(node);
    this.deriveSMTExpr.setUnknown(node);
  }

  @Override
  public void handle(@NotNull ASTIncPrefixExpression node) {
    Preconditions.checkNotNull(node);
    this.deriveSMTExpr.setUnknown(node);
  }

  @Override
  public void handle(@NotNull ASTDecPrefixExpression node) {
    Preconditions.checkNotNull(node);
    this.deriveSMTExpr.setUnknown(node);
  }

  @Override
  public void handle(@NotNull ASTIncSuffixExpression node) {
    Preconditions.checkNotNull(node);
    this.deriveSMTExpr.setUnknown(node);
  }

  @Override
  public void handle(@NotNull ASTDecSuffixExpression node) {
    Preconditions.checkNotNull(node);
    this.deriveSMTExpr.setUnknown(node);
  }
}
