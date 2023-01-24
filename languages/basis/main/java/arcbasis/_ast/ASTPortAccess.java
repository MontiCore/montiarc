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

  @Override
  public PortSymbol getPortSymbol() {
    return portSymbol;
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