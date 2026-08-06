/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import de.monticore.symbols.compsymbols._symboltable.Timing;

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

  public Timing getTiming() {
    return this.sync ? Timing.TIMED_SYNC : Timing.TIMED;
  }
}
