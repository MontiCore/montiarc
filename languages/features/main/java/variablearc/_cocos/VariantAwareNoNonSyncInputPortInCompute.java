/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arccompute._cocos.NoNonSyncInputPortInCompute;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariantAwareNoNonSyncInputPortInContextVisitor;

public class VariantAwareNoNonSyncInputPortInCompute extends NoNonSyncInputPortInCompute {

  public VariantAwareNoNonSyncInputPortInCompute() {
    this(new VariantAwareNoNonSyncInputPortInContextVisitor(context));
  }

  protected VariantAwareNoNonSyncInputPortInCompute(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    super(visit4BasisExpr);
  }
}
