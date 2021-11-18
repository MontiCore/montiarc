/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

/**
 * Represents a port access. Extends {@Link PortAccessTOP} with utility functionality for easy access.
 */
public class ASTPortAccess extends ASTPortAccessTOP {

  public String getQName() {
    if (this.isPresentComponent()) {
      return this.getComponent() + "." + this.getPort();
    } else {
      return this.getPort();
    }
  }

  @Override
  protected void updatePortSymbol() {
    if (getEnclosingScope() != null && (portSymbol == null || !getPort().equals(portSymbol.getName()))) {
      if (isPresentComponent()) {
        if (this.getComponentSymbol() != null && this.getComponentSymbol().getType() != null
          && this.getComponentSymbol().getType().getTypeInfo() != null
          && this.getComponentSymbol().getType().getTypeInfo().getEnclosingScope() != null) {
          portSymbol = getComponentSymbol().getType().getTypeInfo().getSpannedScope().resolvePort(getPort())
            .orElse(null);
        } else {
          portSymbol = null;
        }
      } else {
        portSymbol = getEnclosingScope().resolvePort(getPort()).orElse(null);
      }
    }
  }
}