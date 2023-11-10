/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.ArcPortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
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

  public void setComponentSymbol(@Nullable SubcomponentSymbol symbol) {
    componentSymbol = symbol;
  }

  public void setPortSymbol(@NotNull ArcPortSymbol symbol) {
    Preconditions.checkNotNull(symbol);
    portSymbol = symbol;
  }

  @Override
  public SubcomponentSymbol getComponentSymbol() {
    return componentSymbol;
  }

  @Override
  public ArcPortSymbol getPortSymbol() {
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

  public static ASTPortAccess of(@NotNull ArcPortSymbol port) {
    Preconditions.checkNotNull(port);
    ASTPortAccess p = ArcBasisMill.portAccessBuilder()
      .setPort(port.getName())
      .build();
    p.setPortSymbol(port);
    return p;
  }

  public static ASTPortAccess of(@NotNull SubcomponentSymbol subComp, @NotNull ArcPortSymbol port) {
    Preconditions.checkNotNull(port);
    ASTPortAccess p = ArcBasisMill.portAccessBuilder()
      .setComponent(subComp.getName())
      .setPort(port.getName())
      .build();
    p.setComponentSymbol(subComp);
    p.setPortSymbol(port);
    return p;
  }
}