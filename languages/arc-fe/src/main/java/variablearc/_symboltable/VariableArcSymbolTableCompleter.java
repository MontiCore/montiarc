/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import variablearc._visitor.VariableArcHandler;
import variablearc._visitor.VariableArcTraverser;
import variablearc._visitor.VariableArcVisitor2;

public class VariableArcSymbolTableCompleter implements VariableArcVisitor2, VariableArcHandler {

  protected VariableArcTraverser traverser ;

  @Override
  public void setTraverser(@NotNull VariableArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public VariableArcTraverser getTraverser() {
    return this.traverser;
  }
}