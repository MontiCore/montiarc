/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Represents an auto connect statement.
 */
public class ASTArcAutoConnect extends ASTArcAutoConnectTOP {

  @Override
  public void setArcACMode(@NotNull ASTArcACMode mode) {
    Preconditions.checkArgument(mode != null);
    super.setArcACMode(mode);
  }

  public boolean isACOff() {
    return this.getArcACMode() instanceof ASTArcACOff;
  }

  public boolean isACPortActive() {
    return this.getArcACMode() instanceof ASTArcACPort;
  }

  public boolean isACTypeActive() {
    return this.getArcACMode() instanceof ASTArcACType;
  }
}