/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._cocos.NoPortInSuperComponentArgument;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariantAwareNoPortInStaticContextVisitor;

public class VariantAwareNoPortInSuperComponentArgument extends NoPortInSuperComponentArgument {

  public VariantAwareNoPortInSuperComponentArgument() {
    this(new VariantAwareNoPortInStaticContextVisitor());
  }

  protected VariantAwareNoPortInSuperComponentArgument(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    super(visit4BasisExpr);
  }
}
