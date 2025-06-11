/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcField;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.NoPortInStaticContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that component field declarations do not contain references to ports,
 * as ports are not available in static context.
 */
public class NoPortInFieldDeclaration implements ArcBasisASTArcFieldCoCo {

  protected final ArcBasisTraverser traverser;

  public NoPortInFieldDeclaration() {
    this(new NoPortInStaticContextVisitor());
  }

  public NoPortInFieldDeclaration(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcBasisMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTArcField node) {
    Preconditions.checkNotNull(node);
    node.getInitial().accept(this.traverser);
  }
}
