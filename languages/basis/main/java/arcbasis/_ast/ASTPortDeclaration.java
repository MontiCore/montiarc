/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.timing.Timing;
import arcbasis.timing.TimingCollector;

import java.util.Optional;

/**
 * Represents a port declaration. Extends {@link ASTPortDeclarationTOP} with utility functionality
 * for easy access.
 */
public class ASTPortDeclaration extends ASTPortDeclarationTOP {

  protected ASTPortDeclaration() {
    super();
  }

  public boolean isIncoming() {
    return this.getPortDirection() instanceof ASTPortDirectionIn;
  }

  public boolean isOutgoing() {
    return this.getPortDirection() instanceof ASTPortDirectionOut;
  }

  /**
   * @return the Timing of the port or {@code Optional.empty()} if none is specified
   */
  public Optional<Timing> getTiming() {
    return TimingCollector.getTimings(this.getStereotype()).stream().findFirst();
  }
}