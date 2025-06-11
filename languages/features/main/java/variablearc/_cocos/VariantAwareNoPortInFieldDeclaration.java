/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcbasis._cocos.NoPortInFieldDeclaration;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariantAwareNoPortInStaticContextVisitor;

public class VariantAwareNoPortInFieldDeclaration extends NoPortInFieldDeclaration {

  public VariantAwareNoPortInFieldDeclaration() {
    this(new VariantAwareNoPortInStaticContextVisitor());
  }

  protected VariantAwareNoPortInFieldDeclaration(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    super(visit4BasisExpr);
  }
}
