/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

/**
 * Represents a port access. Extends {@Link PortAccessTOP} with utility functionality
 * for easy access.
 */
public class ASTPortAccess extends ASTPortAccessTOP {

  public String getQName() {
    if (this.isPresentComponent()) {
      return this.getComponent() + "." + this.getPort();
    } else {
      return this.getPort();
    }
  }
}