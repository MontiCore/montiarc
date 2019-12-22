/* (c) https://github.com/MontiCore/monticore */
package arc._ast;

/**
 * Represents a port declaration. Extends {@link ASTPortDeclarationTOP} with utility functionality
 * for easy access.
 */
public class ASTPortDeclaration extends ASTPortDeclarationTOP {

  protected ASTPortDeclaration() {
    super();
  }

  public boolean isIncoming() {
    return getDirection().equals("in");
  }

  public boolean isOutgoing() {
    return getDirection().equals("out");
  }
}