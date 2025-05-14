/* (c) https://github.com/MontiCore/monticore */
package arcbasis._ast;

import arcbasis.ArcBasisMill;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
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

  public void setPortSymbol(@NotNull PortSymbol symbol) {
    Preconditions.checkNotNull(symbol);
    portSymbol = symbol;
  }

  @Override
  protected void updateComponentSymbol() {
    // Don't update if symbol is present
    if (componentSymbol != null) return;

    if (this.isPresentComponent()) {
      this.getEnclosingScope()
        .resolveSubcomponentMany(this.getComponent())
        .stream()
        .findFirst()
        .ifPresent(this::setComponentSymbol);
    }
  }

  @Override
  protected void updatePortSymbol() {
    // Don't update if symbol is present
    if (portSymbol != null) return;

    // Link the port access with the respective port
    // If the port access has a component part,
    // then the port belongs to a subcomponent
    if (this.isPresentComponent()) {
      if (this.isPresentComponentSymbol()
        && this.getComponentSymbol().isTypePresent()
        && this.getComponentSymbol().getType().getTypeInfo() != null
        && this.getComponentSymbol()
        .getType().getTypeInfo().getSpannedScope() != null) {
        (this.getComponentSymbol()
          .getType()
          .getTypeInfo()
          .getSpannedScope())
          .resolvePortMany(this.getPort())
          .stream()
          .findFirst()
          .ifPresent(this::setPortSymbol);
      }
      // else the port belongs to this component
    } else {
      this.getEnclosingScope()
        .resolvePortMany(this.getPort())
        .stream()
        .findFirst()
        .ifPresent(this::setPortSymbol);
    }
  }

  @Override
  public PortSymbol getPortSymbol() {
    // Override to remove error message from super
    updatePortSymbol();
    return portSymbol;
  }

  @Override
  public SubcomponentSymbol getComponentSymbol() {
    // Override to remove error message from super
    updateComponentSymbol();
    return componentSymbol;
  }

  public static ASTPortAccess of(@NotNull PortSymbol port) {
    Preconditions.checkNotNull(port);
    ASTPortAccess p = ArcBasisMill.portAccessBuilder()
      .setPort(port.getName())
      .build();
    p.setPortSymbol(port);
    return p;
  }

  public static ASTPortAccess of(@NotNull SubcomponentSymbol subComp, @NotNull PortSymbol port) {
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