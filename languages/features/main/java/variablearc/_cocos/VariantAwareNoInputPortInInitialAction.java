/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcautomaton._cocos.NoInputPortInInitialAction;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariantAwareNoInputPortInContextVisitor;

public class VariantAwareNoInputPortInInitialAction extends NoInputPortInInitialAction {

  public VariantAwareNoInputPortInInitialAction() {
    this(new VariantAwareNoInputPortInContextVisitor(context));
  }

  protected VariantAwareNoInputPortInInitialAction(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    super(visit4BasisExpr);
  }
}
