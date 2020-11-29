/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Extends the {@link ASTArcAutoConnectBuilderTOP} with utility functions for
 * easy construction of {@link ASTArcAutoConnect} nodes.
 */
public class ASTArcAutoConnectBuilder extends ASTArcAutoConnectBuilderTOP {

  @Override
  public ASTArcAutoConnectBuilder setArcACMode(@NotNull ASTArcACMode mode) {
    Preconditions.checkArgument(mode != null);
    return super.setArcACMode(mode);
  }

  /**
   * Creates a new {@link ASTArcACOff} node and sets its as the auto connect
   * mode for this builder.
   *
   * @see this#setArcACMode(ASTArcACMode)
   */
  public ASTArcAutoConnectBuilder setArcACModeOff() {
    return this.setArcACMode(this.doCreateACOff());
  }

  /**
   * Creates a new {@link ASTArcACPort} node and sets its as the auto connect
   * mode for this builder.
   *
   * @see this#setArcACMode(ASTArcACMode)
   */
  public ASTArcAutoConnectBuilder setArcACModePort() {
    return this.setArcACMode(this.doCreateACPort());
  }

  /**
   * Creates a new {@link ASTArcACType} node and sets its as the auto connect
   * mode for this builder.
   *
   * @see this#setArcACMode(ASTArcACMode)
   */
  public ASTArcAutoConnectBuilder setArcACModeType() {
    return this.setArcACMode(this.doCreateACType());
  }

  public boolean isACModeOff() {
    return this.getArcACMode() instanceof ASTArcACOff;
  }

  public boolean isACModePort() {
    return this.getArcACMode() instanceof ASTArcACPort;
  }

  public boolean isACModeType() {
    return this.getArcACMode() instanceof ASTArcACType;
  }

  protected ASTArcACOff doCreateACOff() {
    return ComfortableArcMill.arcACOffBuilder().build();
  }

  protected ASTArcACPort doCreateACPort() {
    return ComfortableArcMill.arcACPortBuilder().build();
  }

  protected ASTArcACType doCreateACType() {
    return ComfortableArcMill.arcACTypeBuilder().build();
  }
}