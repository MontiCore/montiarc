/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.Optional;

public class ComponentTypeSymbolSurrogate extends ComponentTypeSymbolSurrogateTOP {

  public ComponentTypeSymbolSurrogate(@NotNull String name) {
    super(name);
    this.spannedScope = new ArcBasisScope();
  }

  protected Optional<ComponentTypeSymbol> getDelegate() {
    return this.delegate;
  }

  protected void setDelegate(@Nullable ComponentTypeSymbol delegate) {
    this.delegate = Optional.ofNullable(delegate);
  }

  protected boolean isPresentDelegate() {
    return this.getDelegate().isPresent();
  }

  public ComponentTypeSymbol lazyLoadDelegate() {
    if(!isPresentDelegate()) {
      this.setDelegate(this.getEnclosingScope().resolveComponentType(name).orElse(null));
    }
    return delegate.orElse(this);
  }
}