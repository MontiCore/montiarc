/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.Optional;

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

  public void setComponentSymbol(@Nullable ComponentInstanceSymbol symbol) {
    componentSymbol = symbol;
  }

  public void setPortSymbol(@NotNull PortSymbol symbol) {
    Preconditions.checkNotNull(symbol);
    portSymbol = symbol;
  }

  @Override
  public ComponentInstanceSymbol getComponentSymbol() {
    return componentSymbol;
  }

  public ComponentTypeSymbol getComponentSymbol(ComponentTypeSymbol enclosingComponent) {
    if (!isPresentComponent()) return null;
    Optional<ComponentInstanceSymbol> instance = enclosingComponent.getSubComponent(getComponent());
    if (instance.isEmpty() || !instance.get().isPresentType() || instance.get().getType().getTypeInfo() == null) {
      return null;
    }

    return enclosingComponent.getSubComponent(getComponent()).get().getType().getTypeInfo();
  }

  public boolean isPresentComponentSymbol(ComponentTypeSymbol enclosingComponent) {
    return getComponentSymbol(enclosingComponent) != null;
  }

  @Override
  public PortSymbol getPortSymbol() {
    return portSymbol;
  }

  public PortSymbol getPortSymbol(ComponentTypeSymbol enclosingComponent) {
    if (isPresentComponent()) {
      if (isPresentComponentSymbol(enclosingComponent)) {
        return getComponentSymbol(enclosingComponent).getPort(getPort(), true).orElse(null);
      }
    } else {
      return enclosingComponent.getPort(getPort(), true).orElse(null);
    }
    return null;
  }

  public boolean isPresentPortSymbol(ComponentTypeSymbol enclosingComponent) {
    return getPortSymbol(enclosingComponent) != null;
  }

  @Override
  public boolean isPresentComponentSymbol() {
    return componentSymbol != null;
  }

  @Override
  public boolean isPresentPortSymbol() {
    return portSymbol != null;
  }

  public boolean matches(@NotNull ASTPortAccess portRef) {
    Preconditions.checkNotNull(portRef);
    if (!this.matchesComponent(portRef)) return false;
    else return this.getPort().equals(portRef.getPort());
  }

  public boolean matchesComponent(@NotNull ASTPortAccess portRef) {
    Preconditions.checkNotNull(portRef);
    return (!this.isPresentComponent() && !portRef.isPresentComponent())
      || (this.isPresentComponent() && portRef.isPresentComponent()
      && this.getComponent().equals(portRef.getComponent()));
  }
}