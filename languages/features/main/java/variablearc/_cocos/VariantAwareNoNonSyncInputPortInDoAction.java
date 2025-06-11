/* (c) https://github.com/MontiCore/monticore */
package variablearc._cocos;

import arcautomaton._cocos.NoNonSyncInputPortInDoAction;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariantAwareNoNonSyncInputPortInContextVisitor;

public class VariantAwareNoNonSyncInputPortInDoAction extends NoNonSyncInputPortInDoAction {

  public VariantAwareNoNonSyncInputPortInDoAction() {
    this(new VariantAwareNoNonSyncInputPortInContextVisitor(context));
  }

  protected VariantAwareNoNonSyncInputPortInDoAction(@NotNull ExpressionsBasisVisitor2 visit4BasisExpr) {
    super(visit4BasisExpr);
  }
}
