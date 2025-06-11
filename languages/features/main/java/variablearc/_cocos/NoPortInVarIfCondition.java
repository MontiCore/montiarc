/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.NoPortInStaticContextVisitor;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcVarIf;

/**
 * Checks that varif conditions do not contain references to ports,
 * as ports are not available in static context.
 */
public class NoPortInVarIfCondition implements VariableArcASTArcVarIfCoCo {

  protected final ArcBasisTraverser traverser;

  public NoPortInVarIfCondition() {
    this(new NoPortInStaticContextVisitor());
  }

  protected NoPortInVarIfCondition(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcBasisMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTArcVarIf node) {
    Preconditions.checkNotNull(node);
    node.getCondition().accept(this.traverser);
  }
}
