/* (c) https://github.com/MontiCore/monticore */
package arcautomaton._cocos;

import arcautomaton.ArcAutomatonMill;
import arcautomaton._visitor.ArcAutomatonTraverser;
import arcbasis._visitor.NoInputPortInContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.scactions._ast.ASTSCEntryAction;
import de.monticore.scactions._ast.ASTSCExitAction;
import de.monticore.scactions._cocos.SCActionsASTSCEntryActionCoCo;
import de.monticore.scactions._cocos.SCActionsASTSCExitActionCoCo;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that entry actions do not contain references to input ports.
 * Messages on an input port are only available for the respective message
 * event, but entry actions may be triggered by other events.
 */
public class NoInputPortInEntryAction implements SCActionsASTSCEntryActionCoCo {

  // A human-readable description of the context in which this check is applied
  protected final static String context = "entry actions";

  // When executing the coco, we traverse the ast using a traverser,
  // applying the included visitor only in the context of entry actions.
  protected final ArcAutomatonTraverser traverser;

  public NoInputPortInEntryAction() {
    this(new NoInputPortInContextVisitor(context));
  }

  protected NoInputPortInEntryAction(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcAutomatonMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTSCEntryAction entryAction) {
    Preconditions.checkNotNull(entryAction);
    entryAction.getSCABody().accept(this.traverser);
  }
}
