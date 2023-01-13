/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.timing.Timing;
import arcbasis.timing.TimingCollector;

import java.util.Collections;
import java.util.List;

/**
 * Represents a port declaration. Extends {@link ASTPortDeclarationTOP} with utility functionality
 * for easy access.
 */
public class ASTPortDeclaration extends ASTPortDeclarationTOP {

  protected ASTPortDeclaration() {
    super();
  }

  public boolean isIncoming() {
    return this.getPortDirection().isIn();
  }

  public boolean isOutgoing() {
    return this.getPortDirection().isOut();
  }

  /**
   * @return the specified Timings of the port
   */
  public List<Timing> getTimings() {
    if (this.isPresentStereotype()) {
      return TimingCollector.getTimings(this.getStereotype());
    } else {
      return Collections.emptyList();
    }
  }
}