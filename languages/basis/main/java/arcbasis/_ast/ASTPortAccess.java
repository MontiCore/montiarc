/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

/**
 * Represents a port-access. Extends {@link ASTPortAccessTOP} with utility functionality for easy access.
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
        if (this.getComponentSymbol() != null && this.getComponentSymbol().isPresentType()
          && this.getComponentSymbol().getType().getTypeInfo() != null
          && this.getComponentSymbol().getType().getTypeInfo().getEnclosingScope() != null) {
          portSymbol = getComponentSymbol().getType().getTypeInfo().getSpannedScope().resolvePortMany(getPort())
            .stream().findFirst().orElse(null);
        } else {
          portSymbol = null;
        }
      } else {
        portSymbol = getEnclosingScope().resolvePortMany(getPort()).stream().findFirst().orElse(null);
      }
    }
  }

  @Override
  protected void updateComponentSymbol () {
    if (isPresentComponent()) {
      if (getEnclosingScope() != null && (componentSymbol == null || !getComponent().equals(componentSymbol.getName()))) {
        componentSymbol = getEnclosingScope().resolveComponentInstanceMany(getComponent()).stream().findFirst().orElse(null);
      }
    }
  }

  public void setComponentSymbol(@Nullable ComponentInstanceSymbol symbol) {
    componentSymbol = symbol;
  }

  public void setPortSymbol(@NotNull PortSymbol symbol) {
    Preconditions.checkNotNull(symbol);
    portSymbol = symbol;
  }
}