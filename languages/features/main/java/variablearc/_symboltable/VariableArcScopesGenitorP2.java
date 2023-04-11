/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariableArcHandler;
import variablearc._visitor.VariableArcTraverser;
import variablearc._visitor.VariableArcVisitor2;

public class VariableArcScopesGenitorP2 implements VariableArcVisitor2, VariableArcHandler, ArcBasisVisitor2 {

  protected VariableArcTraverser traverser;

  @Override
  public VariableArcTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(@NotNull VariableArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public void visit(@NotNull ComponentInstanceSymbol node) {
    Preconditions.checkNotNull(node);

    if (node.isPresentType()) {
      node.bindParameters();
    }
  }
}