/* (c) https://github.com/MontiCore/monticore */
package comfortablearc._ast;

import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import org.codehaus.commons.nullanalysis.NotNull;

public class ASTArcAutoInstantiateBuilder extends ASTArcAutoInstantiateBuilderTOP {

  @Override
  public ASTArcAutoInstantiateBuilder setArcAIMode(@NotNull ASTArcAIMode mode) {
    Preconditions.checkNotNull(mode);
    return super.setArcAIMode(mode);
  }

  /**
   * Creates a new {@link ASTArcAIOn} node and sets its as the auto instantiate
   * mode for this builder.
   *
   * @see this#setArcAIMode(ASTArcAIMode)
   */
  public ASTArcAutoInstantiateBuilder setAIModeOn() {
    return this.setArcAIMode(this.doCreateAIOn());
  }

  /**
   * Creates a new {@link ASTArcAIOff} node and sets its as the auto instantiate
   * mode for this builder.
   *
   * @see this#setArcAIMode(ASTArcAIMode)
   */
  public ASTArcAutoInstantiateBuilder setAIModeOff() {
    return this.setArcAIMode(this.doCreateAIOff());
  }

  public boolean isAIModeOn() {
    return this.getArcAIMode() instanceof ASTArcAIOn;
  }

  public boolean isAIModeOff() {
    return this.getArcAIMode() instanceof ASTArcAIOff;
  }

  protected ASTArcAIOff doCreateAIOff() {
    return ComfortableArcMill.arcAIOffBuilder().build();
  }

  protected ASTArcAIOn doCreateAIOn() {
    return ComfortableArcMill.arcAIOnBuilder().build();
  }
}