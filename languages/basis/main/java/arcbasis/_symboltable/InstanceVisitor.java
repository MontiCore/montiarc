/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._visitor.ArcBasisHandler;
import arcbasis._visitor.ArcBasisTraverser;
import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.SubcomponentSymbol;
import de.monticore.symbols.compsymbols._visitor.CompSymbolsHandler;
import de.monticore.symbols.compsymbols._visitor.CompSymbolsTraverser;
import de.monticore.symboltable.ISymbol;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class InstanceVisitor implements ArcBasisHandler, CompSymbolsHandler {

  protected ComponentTypeSymbol component;

  public Optional<ComponentTypeSymbol> asComponent(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    this.component = null;
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.setCompSymbolsHandler(this);
    traverser.setArcBasisHandler(this);
    symbol.accept(traverser);
    return Optional.ofNullable(this.component);
  }

  @Override
  public void handle(ComponentTypeSymbol node) {
    this.component = node;
  }

  protected ArcPortSymbol port;

  public Optional<ArcPortSymbol> asPort(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    this.port = null;
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.setCompSymbolsHandler(this);
    traverser.setArcBasisHandler(this);
    symbol.accept(traverser);
    return Optional.ofNullable(this.port);
  }

  @Override
  public void handle(ArcPortSymbol node) {
    this.port = node;
  }

  protected SubcomponentSymbol subcomponent;

  public Optional<SubcomponentSymbol> asSubcomponent(@NotNull ISymbol symbol) {
    Preconditions.checkNotNull(symbol);
    this.subcomponent = null;
    ArcBasisTraverser traverser = ArcBasisMill.traverser();
    traverser.setCompSymbolsHandler(this);
    traverser.setArcBasisHandler(this);
    symbol.accept(traverser);
    return Optional.ofNullable(this.subcomponent);
  }

  @Override
  public void handle(SubcomponentSymbol node) {
    this.subcomponent = node;
  }

  protected ArcBasisTraverser traverser;
  @Override
  public ArcBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CompSymbolsTraverser traverser) {
    this.traverser = (ArcBasisTraverser) traverser;
  }

  @Override
  public void setTraverser(ArcBasisTraverser traverser) {
    this.traverser = traverser;
  }
}
