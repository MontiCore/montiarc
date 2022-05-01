/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Represents an auto instantiate statement.
 */
public class ASTArcAutoInstantiate extends ASTArcAutoInstantiateTOP {

  @Override
  public void setArcAIMode(@NotNull ASTArcAIMode mode) {
    Preconditions.checkNotNull(mode);
    super.setArcAIMode(mode);
  }

  public boolean isAIOff() {
    return this.getArcAIMode() instanceof ASTArcAIOff;
  }

  public boolean isAIOn() {
    return this.getArcAIMode() instanceof ASTArcAIOn;
  }
}