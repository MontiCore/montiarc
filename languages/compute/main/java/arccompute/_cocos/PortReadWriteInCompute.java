/* (c) https://github.com/MontiCore/monticore */
package arccompute._cocos;

import arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis;
import arccompute.ArcComputeMill;
import arccompute._ast.ASTArcCompute;
import arccompute._visitor.ArcComputeTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import org.codehaus.commons.nullanalysis.NotNull;

import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextState;

/**
 * Checks that expressions of transitions do not read from output ports
 * or write to input ports. Output ports are read-only as messages are gone
 * the moment they are send, and input ports are read-only as messages are
 * only to be received from the context.
 */
public class PortReadWriteInCompute implements ArcComputeASTArcComputeCoCo {

  final ArcComputeTraverser traverser;

  public PortReadWriteInCompute() {
    this(ArcComputeMill.traverser(), new ContextState());
  }

  /**
   * @param t The traverser to traverse the AST
   * @param c The context object to share state between handlers
   */
  protected PortReadWriteInCompute(@NotNull ArcComputeTraverser t,
                                   @NotNull ContextState c) {
    this(t, new PortReadWriteHandler4ExpressionsBasis(c));
  }

  /**
   * @param t  The traverser to traverse the AST
   * @param be The expression basis handler to report read and
   *           write violations for expression basis.
   */
  protected PortReadWriteInCompute(@NotNull ArcComputeTraverser t,
                                   @NotNull ExpressionsBasisHandler be) {
    this.traverser = Preconditions.checkNotNull(t);
    this.traverser.setExpressionsBasisHandler(be);
  }

  @Override
  public void check(@NotNull ASTArcCompute node) {
    Preconditions.checkNotNull(node);
    node.getMCStatement().accept(this.traverser);
  }
}
