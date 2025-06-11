/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.statements.mccommonstatements._ast.ASTCommonForControl;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._ast.ASTForInitByExpressions;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsHandler;
import de.monticore.statements.mccommonstatements._visitor.MCCommonStatementsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextState;
import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextType;

/**
 * Input ports are read-only, and output ports are write-only. This handler detects illegal
 * operations on ports, i.e, reading from an output port or writing to an input port, and reports
 * these as errors.
 * <p>
 * This is the <i>MCCommonStatements</i> fragment of a handler based implementation.
 *
 * @see PortReadWriteHandler4ExpressionsBasis
 * @see PortReadWriteHandler4AssignmentExpressions
 * @see PortReadWriteHandler4CommonExpressions
 */
public class PortReadWriteHandler4MCCommonStatements implements MCCommonStatementsHandler {

  protected MCCommonStatementsTraverser traverser;

  @Override
  public MCCommonStatementsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull MCCommonStatementsTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  protected final ContextState context;

  public PortReadWriteHandler4MCCommonStatements(@NotNull ContextState context) {
    this.context = Preconditions.checkNotNull(context);
  }

  @Override
  public void traverse(@NotNull ASTExpressionStatement node) {
    Preconditions.checkNotNull(node);

    this.context.setType(ContextType.NEUTRAL);
    node.getExpression().accept(this.getTraverser());
    this.context.setType(ContextType.PURE_READ);
  }

  @Override
  public void traverse(@NotNull ASTCommonForControl node) {
    Preconditions.checkNotNull(node);

    if (node.isPresentForInit()) {
      node.getForInit().accept(this.getTraverser());
    }
    if (node.isPresentCondition()) {
      node.getCondition().accept(this.getTraverser());
    }

    this.context.setType(ContextType.NEUTRAL);
    for (ASTExpression astExpression : node.getExpressionList()) {
      astExpression.accept(this.getTraverser());
    }
    this.context.setType(ContextType.PURE_READ);
  }

  @Override
  public void traverse(@NotNull ASTForInitByExpressions node) {
    Preconditions.checkNotNull(node);

    this.context.setType(ContextType.NEUTRAL);
    for (ASTExpression astExpression : node.getExpressionList()) {
      astExpression.accept(this.getTraverser());
    }
    this.context.setType(ContextType.PURE_READ);
  }
}
