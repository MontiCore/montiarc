/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.NoFieldInStaticContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._ast.ASTArcConstraintDeclaration;

/**
 * Checks that constraints do not contain references to fields,
 * as fields are not available in static context.
 */
public class NoFieldInConstraint implements VariableArcASTArcConstraintDeclarationCoCo {

  protected final ArcBasisTraverser traverser;

  public NoFieldInConstraint() {
    this(new NoFieldInStaticContextVisitor());
  }

  protected NoFieldInConstraint(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcBasisMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTArcConstraintDeclaration node) {
    Preconditions.checkNotNull(node);
    node.getExpression().accept(this.traverser);
  }
}
