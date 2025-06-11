/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.NoPortInStaticContextVisitor;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;

/**
 * Checks that constraints do not contain references to ports,
 * as ports are not available in static context.
 */
public class NoPortInConstraint implements VariableArcASTArcConstraintDeclarationCoCo {

  protected final ArcBasisTraverser traverser;

  public NoPortInConstraint() {
    this(new NoPortInStaticContextVisitor());
  }

  protected NoPortInConstraint(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcBasisMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTArcConstraintDeclaration node) {
    Preconditions.checkNotNull(node);
    node.getExpression().accept(this.traverser);
  }
}
