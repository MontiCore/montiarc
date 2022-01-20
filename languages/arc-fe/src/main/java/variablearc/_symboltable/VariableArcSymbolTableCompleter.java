/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariableArcHandler;
import variablearc._visitor.VariableArcTraverser;
import variablearc._visitor.VariableArcVisitor2;
import variablearc.check.TypeExprOfVariableComponent;

public class VariableArcSymbolTableCompleter implements VariableArcVisitor2, VariableArcHandler, ArcBasisVisitor2 {

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
    Preconditions.checkNotNull(node.getType());

    if (node.getType() instanceof TypeExprOfVariableComponent) {
      node.setType(((TypeExprOfVariableComponent) node.getType()).bindParameters(node.getArguments()));
    }
  }
}