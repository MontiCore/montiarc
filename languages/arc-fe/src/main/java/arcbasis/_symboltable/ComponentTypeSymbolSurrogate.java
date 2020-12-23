/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import genericarc._symboltable.ArcTypeParameterSymbol;
import genericarc._symboltable.IGenericArcScope;
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
    if (!isPresentDelegate()) {
      this.setDelegate(this.getEnclosingScope().resolveComponentType(this.getName()).orElse(this.getEnclosingScope() instanceof IGenericArcScope ?
        tryGeneric().orElse(null) : null));
    }
    return delegate.orElse(this);
  }

  protected Optional<ComponentTypeSymbol> tryGeneric() {
    Preconditions.checkState(this.getEnclosingScope() instanceof IGenericArcScope);
    Optional<ArcTypeParameterSymbol> resolvedTypeSymbol =
      ((IGenericArcScope) this.getEnclosingScope()).resolveArcTypeParameter(this.getName());
    if (resolvedTypeSymbol.isPresent()) {
      ComponentTypeSymbol resolvedSymbol = this.getEnclosingScope().resolveComponentType(resolvedTypeSymbol.get().getAstNode().getUpperBound(0).printType(new MCBasicTypesPrettyPrinter(new IndentPrinter()))).orElse(null);
      return Optional.ofNullable(resolvedSymbol);
    }
    return Optional.empty();
  }
}