/* (c) https://github.com/MontiCore/monticore */
package arcbasis._cocos.util;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.SourcePosition;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.HashMap;
import java.util.HashSet;

import static com.google.common.base.Preconditions.checkNotNull;

public class PortReferenceExtractor4ExpressionBasis
  implements ExpressionsBasisHandler, IPortReferenceInExpressionExtractor {

  protected HashSet<PortReference> portReferencesToLookFor;
  protected final HashMap<PortReference, SourcePosition> foundPortReferences;
  protected ExpressionsBasisTraverser traverser;

  public PortReferenceExtractor4ExpressionBasis() {
    this.portReferencesToLookFor = new HashSet<>();
    this.foundPortReferences = new HashMap<>();
    this.setTraverser(ExpressionsBasisMill.traverser());
  }

  @Override
  public ExpressionsBasisTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(@NotNull ExpressionsBasisTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  public HashMap<PortReference, SourcePosition> getFoundPortReferences() {
    return this.foundPortReferences;
  }

  public void clearFoundPortReferences() {
    this.foundPortReferences.clear();
  }

  public HashSet<PortReference> getPortReferencesToLookFor() {
    return this.portReferencesToLookFor;
  }

  protected void setPortReferencesToLookFor(@NotNull HashSet<PortReference> portReferencesToLookFor) {
    this.portReferencesToLookFor = checkNotNull(portReferencesToLookFor);
  }

  @Override
  public void handle(@NotNull ASTNameExpression expr) {
    Preconditions.checkNotNull(expr);

    this.getPortReferencesToLookFor().stream()
      .filter(pr -> !pr.getInstanceName().isPresent())
      .filter(pr -> pr.getPortName().equals(expr.getName()))
      .forEach(pr -> foundPortReferences.put(pr, expr.get_SourcePositionStart()));
  }

  @Override
  public HashMap<PortReference, SourcePosition> findPortReferences(@NotNull ASTExpression expr,
                                                                   @NotNull HashSet<PortReference> portReferencesToLookFor,
                                                                   @NotNull ITraverser traverser) {
    Preconditions.checkNotNull(expr);
    Preconditions.checkNotNull(portReferencesToLookFor);
    Preconditions.checkNotNull(traverser);
    Preconditions.checkArgument(traverser instanceof ExpressionsBasisTraverser);

    this.clearFoundPortReferences();
    this.setPortReferencesToLookFor(portReferencesToLookFor);
    this.setTraverser((ExpressionsBasisTraverser) traverser);

    this.getTraverser().setExpressionsBasisHandler(this);
    expr.accept(traverser);

    return this.getFoundPortReferences();
  }
}
