/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcautomaton._cocos.NoInputPortInExitAction;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariantAwareNoInputPortInContextVisitor;

public class VariantAwareNoInputPortInExitAction extends NoInputPortInExitAction {

  public VariantAwareNoInputPortInExitAction() {
    this(new VariantAwareNoInputPortInContextVisitor(context));
  }

  protected VariantAwareNoInputPortInExitAction(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    super(visit4BasisExpr);
  }
}
