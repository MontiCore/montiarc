/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.scdoactions._ast.ASTSCDoAction;
import de.monticore.scdoactions._cocos.SCDoActionsASTSCDoActionCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextState;

/**
 * Checks that expressions of do-actions do not read from output ports
 * or write to input ports. Output ports are read-only as messages are gone
 * the moment they are send, and input ports are read-only as messages are
 * only to be received from the context.
 */
public class PortReadWriteInDoAction implements SCDoActionsASTSCDoActionCoCo {

  final ArcAutomatonTraverser traverser;

  public PortReadWriteInDoAction() {
    this(ArcAutomatonMill.traverser(), new ContextState());
  }

  /**
   * @param t  The traverser to traverse the AST
   * @param c The context object to share state between handlers
   */
  protected PortReadWriteInDoAction(@NotNull ArcAutomatonTraverser t,
                                    @NotNull ContextState c) {
    this(t, new PortReadWriteHandler4ExpressionsBasis(c));
  }

  /**
   * @param t The traverser to traverse the AST
   * @param be The expression basis handler to report read and
   *           write violations for expression basis.
   */
  protected PortReadWriteInDoAction(@NotNull ArcAutomatonTraverser t,
                                    @NotNull ExpressionsBasisHandler be) {
    this.traverser = Preconditions.checkNotNull(t);
    this.traverser.setExpressionsBasisHandler(be);
  }

  @Override
  public void check(@NotNull ASTSCDoAction node) {
    Preconditions.checkNotNull(node);
    node.accept(this.traverser);
  }
}
