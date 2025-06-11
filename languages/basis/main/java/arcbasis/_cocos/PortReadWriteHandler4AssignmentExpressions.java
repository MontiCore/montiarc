/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTDecSuffixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTIncSuffixExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsHandler;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextState;
import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextType;

/**
 * Input ports are read-only, and output ports are write-only. This handler detects illegal
 * operations on ports, i.e, reading from an output port or writing to an input port, and reports
 * these as errors.
 * <p>
 * This is the <i>AssignmentExpressions</i> fragment of a handler based implementation.
 *
 * @see PortReadWriteHandler4ExpressionsBasis
 * @see PortReadWriteHandler4MCCommonStatements
 */
public class PortReadWriteHandler4AssignmentExpressions implements AssignmentExpressionsHandler {

  protected AssignmentExpressionsTraverser traverser;

  @Override
  public AssignmentExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull AssignmentExpressionsTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  protected final ContextState context;

  public PortReadWriteHandler4AssignmentExpressions(@NotNull ContextState context) {
    this.context = Preconditions.checkNotNull(context);
  }

  @Override
  public void handle(@NotNull ASTIncSuffixExpression expr) {
    Preconditions.checkNotNull(expr);

    ContextType pre = this.context.getType();

    this.context.setType(ContextType.READ_AND_WRITE);
    AssignmentExpressionsHandler.super.handle(expr);
    this.context.setType(pre);
  }

  @Override
  public void handle(@NotNull ASTDecSuffixExpression expr) {
    Preconditions.checkNotNull(expr);

    this.context.setType(ContextType.READ_AND_WRITE);
    AssignmentExpressionsHandler.super.handle(expr);
  }

  @Override
  public void handle(@NotNull ASTIncPrefixExpression expr) {
    Preconditions.checkNotNull(expr);

    ContextType pre = this.context.getType();

    this.context.setType(ContextType.READ_AND_WRITE);
    AssignmentExpressionsHandler.super.handle(expr);
    this.context.setType(pre);
  }

  @Override
  public void handle(@NotNull ASTDecPrefixExpression expr) {
    Preconditions.checkNotNull(expr);

    ContextType pre = this.context.getType();

    this.context.setType(ContextType.READ_AND_WRITE);
    AssignmentExpressionsHandler.super.handle(expr);
    this.context.setType(pre);
  }

  @Override
  public void traverse(@NotNull ASTAssignmentExpression expr) {
    Preconditions.checkNotNull(expr);

    ContextType pre = this.context.getType();

    if (expr.getOperator() != ASTConstantsAssignmentExpressions.EQUALS) {
      this.context.setType(ContextType.READ_AND_WRITE);
    } else if (pre == ContextType.PURE_READ) {
      this.context.setType(ContextType.READ_AND_WRITE);
    } else {
      this.context.setType(ContextType.PURE_WRITE);
    }

    expr.getLeft().accept(this.getTraverser());

    this.context.setType(ContextType.PURE_READ);
    expr.getRight().accept(this.getTraverser());
    this.context.setType(pre);
  }
}
