/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis._visitor.NoInputPortInContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._cocos.SCBasisASTSCStateCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that statechart antes (initial action of a statechart) do not
 * contain references to input ports, as initial actions are executed before
 * any messages are received.
 */
public class NoInputPortInInitialAction implements SCBasisASTSCStateCoCo {

  // A human-readable description of the context in which this check is applied
  protected final static String context = "initial actions";

  // When executing the coco, we traverse the ast using a traverser,
  // applying the included visitor only in the context of initial actions.
  protected final ArcAutomatonTraverser traverser;

  public NoInputPortInInitialAction() {
    this(new NoInputPortInContextVisitor(context));
  }

  protected NoInputPortInInitialAction(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcAutomatonMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTSCState state) {
    Preconditions.checkNotNull(state);
    if (state.isPresentSCSAnte()) state.getSCSAnte().accept(this.traverser);
  }
}
