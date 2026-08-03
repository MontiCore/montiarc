/* (c) https://github.com/MontiCore/monticore */
package arcags._cocos;

import arcags.ArcAGsMill;
import arcags._ast.ASTArcAG;
import arcags._visitor.ArcAGsTraverser;
import arcbasis._visitor.NoFieldInStaticContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

public class NoFieldInAGExpression implements ArcAGsASTArcAGCoCo {

  protected final ArcAGsTraverser traverser;

  public NoFieldInAGExpression() {
    this(new NoFieldInStaticContextVisitor());
  }

  protected NoFieldInAGExpression(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    Preconditions.checkNotNull(visit4BasisExpr);
    this.traverser = ArcAGsMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(ASTArcAG node) {
    Preconditions.checkNotNull(node);
    node.getGuarantee().accept(this.traverser);
    if (node.isPresentAssume()) {
      node.getAssume().accept(this.traverser);
    }
  }
}
