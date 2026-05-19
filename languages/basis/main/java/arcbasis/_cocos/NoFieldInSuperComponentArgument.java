/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcParent;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.NoFieldInStaticContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

public class NoFieldInSuperComponentArgument implements ArcBasisASTArcParentCoCo{

  protected final ArcBasisTraverser traverser;

  public NoFieldInSuperComponentArgument() {
    this(new NoFieldInStaticContextVisitor());
  }

  public NoFieldInSuperComponentArgument(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcBasisMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTArcParent node) {
    Preconditions.checkNotNull(node);
    for(ASTArcArgument arg : node.getArcArgumentList()) {
      arg.accept(this.traverser);
    }
  }
}
