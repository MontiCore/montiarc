/* (c) https://github.com/MontiCore/monticore */
package arccompute._cocos;

import arcbasis._visitor.NoNonSyncInputPortInContextVisitor;
import arccompute.ArcComputeMill;
import arccompute._ast.ASTArcCompute;
import arccompute._visitor.ArcComputeTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that compute statements do not contain references to non-synchronous
 * ports, as only synchronous ports are available during a time event.
 */
public class NoNonSyncInputPortInCompute implements ArcComputeASTArcComputeCoCo {

  // A human-readable description of the context in which this check is applied
  public final static String CONTEXT = "compute blocks";

  // When executing the coco, we traverse the ast using a traverser,
  // applying the included visitor only in the context of compute blocks.
  protected final ArcComputeTraverser traverser;

  public NoNonSyncInputPortInCompute() {
    this(new NoNonSyncInputPortInContextVisitor(CONTEXT));
  }

  protected NoNonSyncInputPortInCompute(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcComputeMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTArcCompute node) {
    Preconditions.checkNotNull(node);
    node.accept(this.traverser);
  }
}
