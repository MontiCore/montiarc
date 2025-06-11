/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._cocos.NoPortInDefaultParameterValue;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariantAwareNoPortInStaticContextVisitor;

public class VariantAwareNoPortInDefaultParameterValue extends NoPortInDefaultParameterValue {

  public VariantAwareNoPortInDefaultParameterValue() {
    this(new VariantAwareNoPortInStaticContextVisitor());
  }

  protected VariantAwareNoPortInDefaultParameterValue(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    super(visit4BasisExpr);
  }
}
