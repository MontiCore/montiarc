/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcParameter;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.NoFieldInStaticContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

public class NoFieldInDefaultParameterValue implements ArcBasisASTArcParameterCoCo {
  protected final ArcBasisTraverser traverser;

  public NoFieldInDefaultParameterValue() {
    this(new NoFieldInStaticContextVisitor());
  }

  protected NoFieldInDefaultParameterValue(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcBasisMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTArcParameter node) {
    Preconditions.checkNotNull(node);
    if (node.isPresentDefault()) {
      node.getDefault().accept(this.traverser);
    }
  }
}
