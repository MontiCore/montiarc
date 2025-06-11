/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionBodyCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

import static arcbasis._cocos.PortReadWriteHandler4ExpressionsBasis.ContextState;

/**
 * Checks that expressions of transitions do not read from output ports
 * or write to input ports. Output ports are read-only as messages are gone
 * the moment they are send, and input ports are read-only as messages are
 * only to be received from the context.
 */
public class PortReadWriteInTransition implements SCTransitions4CodeASTTransitionBodyCoCo {

  final ArcAutomatonTraverser traverser;

  public PortReadWriteInTransition() {
    this(ArcAutomatonMill.traverser(), new ContextState());
  }

  /**
   * @param t  The traverser to traverse the AST
   * @param c The context object to share state between handlers
   */
  protected PortReadWriteInTransition(@NotNull ArcAutomatonTraverser t,
                                      @NotNull ContextState c) {
    this(t, new PortReadWriteHandler4ExpressionsBasis(c));
  }

  /**
   * @param t The traverser to traverse the AST
   * @param be The expression basis handler to report read and
   *           write violations for expression basis.
   */
  protected PortReadWriteInTransition(@NotNull ArcAutomatonTraverser t,
                                      @NotNull ExpressionsBasisHandler be) {
    this.traverser = Preconditions.checkNotNull(t);
    this.traverser.setExpressionsBasisHandler(be);
  }

  @Override
  public void check(@NotNull ASTTransitionBody node) {
    Preconditions.checkNotNull(node);
    if (node.isPresentPre()) {
      node.getPre().accept(this.traverser);
    }
    if (node.isPresentTransitionAction()) {
      node.getTransitionAction().accept(this.traverser);
    }
  }
}
