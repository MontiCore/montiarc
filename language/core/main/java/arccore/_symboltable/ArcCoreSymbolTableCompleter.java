/* (c) https://github.com/MontiCore/monticore */
package arccore._symboltable;

import arccore._visitor.ArcCoreHandler;
import arccore._visitor.ArcCoreTraverser;
import arccore._visitor.ArcCoreVisitor2;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcCoreSymbolTableCompleter implements ArcCoreVisitor2, ArcCoreHandler {

  protected ArcCoreTraverser traverser ;

  @Override
  public void setTraverser(@NotNull ArcCoreTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public ArcCoreTraverser getTraverser() {
    return this.traverser;
  }
}