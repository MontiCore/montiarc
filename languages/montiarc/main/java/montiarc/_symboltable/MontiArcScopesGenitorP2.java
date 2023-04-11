/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import com.google.common.base.Preconditions;
import montiarc._visitor.MontiArcHandler;
import montiarc._visitor.MontiArcTraverser;
import montiarc._visitor.MontiArcVisitor2;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcScopesGenitorP2 implements MontiArcVisitor2, MontiArcHandler {

  protected MontiArcTraverser traverser ;

  @Override
  public void setTraverser(@NotNull MontiArcTraverser traverser) {
    Preconditions.checkNotNull(traverser);
    this.traverser = traverser;
  }

  @Override
  public MontiArcTraverser getTraverser() {
    return this.traverser;
  }
}