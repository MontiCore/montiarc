/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import org.codehaus.commons.nullanalysis.NotNull;

import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextState;
import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextType;

/**
 * Input ports are read-only, and output ports are write-only. This handler detects illegal
 * operations on ports, i.e, reading from an output port or writing to an input port, and reports
 * these as errors.
 * <p>
 * This is the <i>CommonExpressions</i> fragment of a handler based implementation.
 *
 * @see PortReadWriteHandler4ExpressionsBasis
 * @see PortReadWriteHandler4AssignmentExpressions
 * @see PortReadWriteHandler4MCCommonStatements
 */
public class PortReadWriteHandler4CommonExpressions implements CommonExpressionsHandler {

  protected CommonExpressionsTraverser traverser;

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull CommonExpressionsTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  protected final ContextState context;

  public PortReadWriteHandler4CommonExpressions(@NotNull ContextState context) {
    this.context = Preconditions.checkNotNull(context);
  }

  @Override
  public void handle(@NotNull ASTFieldAccessExpression node) {
    Preconditions.checkNotNull(node);

    ContextType pre = this.context.getType();

    if (this.context.isInWriteContext()) {
      this.context.setType(ContextType.READ_AND_WRITE);
    } else {
      this.context.setType(ContextType.PURE_READ);
    }
    node.getExpression().accept(this.getTraverser());
    this.context.setType(pre);
  }
}
