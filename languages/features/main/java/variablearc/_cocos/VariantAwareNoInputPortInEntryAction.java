/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcautomaton._cocos.NoInputPortInEntryAction;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariantAwareNoInputPortInContextVisitor;

public class VariantAwareNoInputPortInEntryAction extends NoInputPortInEntryAction {

  public VariantAwareNoInputPortInEntryAction() {
    this(new VariantAwareNoInputPortInContextVisitor(context));
  }

  protected VariantAwareNoInputPortInEntryAction(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    super(visit4BasisExpr);
  }
}
