/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcParent;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.NoPortInStaticContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that super component arguments do not contain references to ports,
 * as ports are not available in static context.
 */
public class NoPortInSuperComponentArgument implements ArcBasisASTArcParentCoCo {

  protected final ArcBasisTraverser traverser;

  public NoPortInSuperComponentArgument() {
    this(new NoPortInStaticContextVisitor());
  }

  public NoPortInSuperComponentArgument(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
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
