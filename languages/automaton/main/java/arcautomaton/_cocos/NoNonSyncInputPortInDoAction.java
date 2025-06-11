/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis._visitor.NoNonSyncInputPortInContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.scdoactions._ast.ASTSCDoAction;
import de.monticore.scdoactions._cocos.SCDoActionsASTSCDoActionCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that do actions do not contain references to non-synchronous ports,
 * as only synchronous ports are available during a time event.
 */
public class NoNonSyncInputPortInDoAction implements SCDoActionsASTSCDoActionCoCo {

  // A human-readable description of the context in which this check is applied
  protected final static String context = "do actions";

  // When executing the coco, we traverse the ast using a traverser,
  // applying the included visitor only in the context of do actions.
  protected final ArcAutomatonTraverser traverser;

  public NoNonSyncInputPortInDoAction() {
    this(new NoNonSyncInputPortInContextVisitor(context));
  }

  protected NoNonSyncInputPortInDoAction(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcAutomatonMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTSCDoAction node) {
    Preconditions.checkNotNull(node);
    node.accept(this.traverser);
  }
}
