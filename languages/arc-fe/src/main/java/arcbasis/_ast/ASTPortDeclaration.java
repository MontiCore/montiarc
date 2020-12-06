/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

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
}