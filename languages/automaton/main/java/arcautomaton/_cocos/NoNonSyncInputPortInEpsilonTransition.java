/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis._visitor.NoNonSyncInputPortInContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.sctransitions4code._cocos.SCTransitions4CodeASTTransitionBodyCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that transitions triggered by tick events do not contain references
 * to non-synchronous ports, as only synchronous ports are available during a
 * time event.
 */
public class NoNonSyncInputPortInEpsilonTransition implements SCTransitions4CodeASTTransitionBodyCoCo {

  // A human-readable description of the context in which this check is applied
  protected final static String context = "time-event triggered transitions";

  // When executing the coco, we traverse the ast using a traverser,
  // applying the included visitor only in the context of time-event triggered transitions.
  protected final ArcAutomatonTraverser traverser;

  public NoNonSyncInputPortInEpsilonTransition() {
    this(new NoNonSyncInputPortInContextVisitor(context));
  }

  protected NoNonSyncInputPortInEpsilonTransition(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcAutomatonMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTTransitionBody node) {
    Preconditions.checkNotNull(node);

    if (!node.isPresentSCEvent()) {
      node.accept(this.traverser);
    }
  }
}
