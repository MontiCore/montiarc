/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arccompute._cocos.NoInputPortsInInitialCompute;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariantAwareNoInputPortInContextVisitor;

public class VariantAwareNoInputPortsInInitialCompute extends NoInputPortsInInitialCompute {

  public VariantAwareNoInputPortsInInitialCompute() {
    this(new VariantAwareNoInputPortInContextVisitor(context));
  }

  protected VariantAwareNoInputPortsInInitialCompute(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    super(visit4BasisExpr);
  }
}
