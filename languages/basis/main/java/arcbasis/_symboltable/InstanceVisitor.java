/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class InstanceVisitor implements ArcBasisHandler {

  protected ComponentTypeSymbol component;

  public Optional<ComponentTypeSymbol> asComponent(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    this.component = null;
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.setArcBasisHandler(this);
    symbol.accept(traverser);
    return Optional.ofNullable(this.component);
  }

  @Override
  public void handle(ComponentTypeSymbol node) {
    this.component = node;
  }

  protected PortSymbol port;

  public Optional<PortSymbol> asPort(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    this.port = null;
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.setArcBasisHandler(this);
    symbol.accept(traverser);
    return Optional.ofNullable(this.port);
  }

  @Override
  public void handle(PortSymbol node) {
    this.port = node;
  }

  protected ComponentInstanceSymbol subcomponent;

  public Optional<ComponentInstanceSymbol> asSubcomponent(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    this.subcomponent = null;
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.setArcBasisHandler(this);
    symbol.accept(traverser);
    return Optional.ofNullable(this.subcomponent);
  }

  @Override
  public void handle(ComponentInstanceSymbol node) {
    this.subcomponent = node;
  }

  protected ArcBasisTraverser traverser;
  @Override
  public ArcBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(ArcBasisTraverser traverser) {
    this.traverser = traverser;
  }
}
