/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._cocos.NoPortInSubcomponentArgument;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariantAwareNoPortInStaticContextVisitor;

public class VariantAwareNoPortInSubcomponentArgument extends NoPortInSubcomponentArgument {

  public VariantAwareNoPortInSubcomponentArgument() {
    this(new VariantAwareNoPortInStaticContextVisitor());
  }

  protected VariantAwareNoPortInSubcomponentArgument(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    super(visit4BasisExpr);
  }
}
