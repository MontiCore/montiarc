/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._symboltable;

import com.google.common.base.Preconditions;
import comfortablearc._visitor.ComfortableArcHandler;
import comfortablearc._visitor.ComfortableArcTraverser;
import comfortablearc._visitor.ComfortableArcVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

public class ComfortableArcSymbolTableCompleter implements ComfortableArcVisitor2, ComfortableArcHandler {

  protected ComfortableArcTraverser traverser ;

  @Override
  public void setTraverser(@NotNull ComfortableArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public ComfortableArcTraverser getTraverser() {
    return this.traverser;
  }
}