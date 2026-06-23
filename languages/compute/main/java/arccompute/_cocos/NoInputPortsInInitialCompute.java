/* (c) https://github.com/MontiCore/monticore */
package arccompute._cocos;

import arcbasis._visitor.NoInputPortInContextVisitor;
import arccompute.ArcComputeMill;
import arccompute._ast.ASTArcInit;
import arccompute._visitor.ArcComputeTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that initial actions do not contain references to input ports, as
 * initial actions are executed before any messages are received.
 */
public class NoInputPortsInInitialCompute implements ArcComputeASTArcInitCoCo {

  // A human-readable description of the context in which this check is applied
  public final static String CONTEXT = "initial action";

  // When executing the coco, we traverse the ast using a traverser,
  // applying the included visitor only in the context of initial actions.
  protected final ArcComputeTraverser traverser;

  public NoInputPortsInInitialCompute() {
    this(new NoInputPortInContextVisitor(CONTEXT));
  }

  protected NoInputPortsInInitialCompute(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcComputeMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTArcInit node) {
    Preconditions.checkNotNull(node);
    node.getMCStatement().accept(this.traverser);
  }
}
