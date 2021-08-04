/* (c) https://github.com/MontiCore/monticore */
package genericarc._symboltable;

import com.google.common.base.Preconditions;
import genericarc._visitor.GenericArcHandler;
import genericarc._visitor.GenericArcTraverser;
import genericarc._visitor.GenericArcVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

public class GenericArcSymbolTableCompleter implements GenericArcVisitor2, GenericArcHandler {

  protected GenericArcTraverser traverser ;

  @Override
  public void setTraverser(@NotNull GenericArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public GenericArcTraverser getTraverser() {
    return this.traverser;
  }
}