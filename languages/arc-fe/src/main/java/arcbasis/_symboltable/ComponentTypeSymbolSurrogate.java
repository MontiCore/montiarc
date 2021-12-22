/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.Optional;

public class ComponentTypeSymbolSurrogate extends ComponentTypeSymbolSurrogateTOP {

  public ComponentTypeSymbolSurrogate(@NotNull String name) {
    super(name);
    this.spannedScope = ArcBasisMill.scope();
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
    if (!isPresentDelegate()) {
      this.setDelegate(this.getEnclosingScope().resolveComponentType(this.getName()).orElse(tryGeneric().orElse(null)));
    }
    return delegate.orElse(this);
  }

  protected Optional<ComponentTypeSymbol> tryGeneric() {
    Optional<TypeVarSymbol> resolvedTypeSymbol = this.getEnclosingScope().resolveTypeVar(this.getName());
    if (resolvedTypeSymbol.isPresent()) {
      ComponentTypeSymbol resolvedSymbol = this.getEnclosingScope().resolveComponentType(resolvedTypeSymbol.get().getSuperTypes(0).printFullName()).orElse(null);
      return Optional.ofNullable(resolvedSymbol);
    }
    return Optional.empty();
  }
}