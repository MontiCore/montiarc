/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis._visitor.NoInputPortInContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.scactions._ast.ASTSCExitAction;
import de.monticore.scactions._cocos.SCActionsASTSCExitActionCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that exit actions do not contain references to input ports.
 * Messages on an input port are only available for the respective message
 * event, but exit actions may be triggered by other events.
 */
public class NoInputPortInExitAction implements SCActionsASTSCExitActionCoCo {

  // A human-readable description of the context in which this check is applied
  public final static String CONTEXT = "exit actions";

  // When executing the coco, we traverse the ast using a traverser,
  // applying the included visitor only in the context of exit actions.
  protected final ArcAutomatonTraverser traverser;

  public NoInputPortInExitAction() {
    this(new NoInputPortInContextVisitor(CONTEXT));
  }

  protected NoInputPortInExitAction(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcAutomatonMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTSCExitAction exitAction) {
    Preconditions.checkNotNull(exitAction);
    exitAction.getSCABody().accept(this.traverser);
  }
}
