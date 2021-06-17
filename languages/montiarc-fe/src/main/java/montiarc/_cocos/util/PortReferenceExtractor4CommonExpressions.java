/* (c) https://github.com/MontiCore/monticore */
package montiarc._cocos.util;

import arcbasis._cocos.util.PortReferenceExtractor4ExpressionBasis;
import com.google.common.base.Preconditions;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashMap;
import java.util.HashSet;


public class PortReferenceExtractor4CommonExpressions extends PortReferenceExtractor4ExpressionBasis
  implements CommonExpressionsHandler {

  protected CommonExpressionsTraverser traverser;

  public PortReferenceExtractor4CommonExpressions() {
    super();
    this.setTraverser(CommonExpressionsMill.traverser());
  }

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(@NotNull CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void setTraverser(@NotNull ExpressionsBasisTraverser traverser) {
    Preconditions.checkArgument(traverser instanceof CommonExpressionsTraverser);
    this.setTraverser((CommonExpressionsTraverser) traverser);
    super.setTraverser(traverser);
  }

  @Override
  public void handle(@NotNull ASTFieldAccessExpression expr) {
    Preconditions.checkNotNull(expr);

    if (expr.getExpression() instanceof ASTNameExpression) {
      String instanceName = ((ASTNameExpression) expr.getExpression()).getName();
      String portName = expr.getName();

      this.getPortReferencesToLookFor().stream()
        .filter(pr -> pr.getInstanceName().isPresent())
        .filter(pr -> portName.equals(pr.getPortName()))
        .filter(pr -> instanceName.equals(pr.getInstanceName().get()))
        .forEach(pr -> foundPortReferences.put(pr, expr.get_SourcePositionStart()));
    } else {
      super.handle(expr);
    }
  }

  @Override
  public HashMap<PortReference, SourcePosition> findPortReferences(@NotNull ASTExpression expr,
                                                                   @NotNull HashSet<PortReference> portReferencesToLookFor,
                                                                   @NotNull ITraverser traverser) {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(portReferencesToLookFor);
    Preconditions.checkNotNull(traverser);
    Preconditions.checkArgument(traverser instanceof CommonExpressionsTraverser);

    this.clearFoundPortReferences();
    this.setPortReferencesToLookFor(portReferencesToLookFor);
    this.setTraverser((CommonExpressionsTraverser) traverser);

    this.getTraverser().setExpressionsBasisHandler(this);
    this.getTraverser().setCommonExpressionsHandler(this);
    expr.accept(traverser);

    return this.getFoundPortReferences();
  }
}
