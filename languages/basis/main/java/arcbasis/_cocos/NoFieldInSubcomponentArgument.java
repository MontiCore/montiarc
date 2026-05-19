/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._visitor.ArcBasisTraverser;
import arcbasis._visitor.NoFieldInStaticContextVisitor;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Checks that subcomponent arguments do not contain references to fields,
 * as fields are not available in static context.
 */
public class NoFieldInSubcomponentArgument implements ArcBasisASTComponentInstanceCoCo {

  protected final ArcBasisTraverser traverser;

  public NoFieldInSubcomponentArgument() {
    this(new NoFieldInStaticContextVisitor());
  }

  public NoFieldInSubcomponentArgument(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    this.traverser = ArcBasisMill.traverser();
    this.traverser.add4ExpressionsBasis(Preconditions.checkNotNull(visit4BasisExpr));
  }

  @Override
  public void check(@NotNull ASTComponentInstance node) {
    Preconditions.checkNotNull(node);
    if (node.isPresentArcArguments()) {
      node.getArcArguments().accept(this.traverser);
    }
  }
}
