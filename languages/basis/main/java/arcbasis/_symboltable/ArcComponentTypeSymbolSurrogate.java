/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcBehaviorElement;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ArcComponentTypeSymbolSurrogate extends ArcComponentTypeSymbolSurrogateTOP {

  public ArcComponentTypeSymbolSurrogate(@NotNull String name) {
    super(name);
    this.spannedScope = ArcBasisMill.scope();
  }

  protected Optional<ArcComponentTypeSymbol> getDelegate() {
    return this.delegate;
  }

  protected void setDelegate(@Nullable ArcComponentTypeSymbol delegate) {
    this.delegate = Optional.ofNullable(delegate);
  }

  @Override
  public ArcComponentTypeSymbol lazyLoadDelegate() {
    if (this.getDelegate().isEmpty()) {
      this.setDelegate(this.getEnclosingScope().resolveArcComponentType(this.getName()).orElse(tryGeneric().orElse(null)));
    }

    if (this.getDelegate().isPresent()) {
      return this.getDelegate().get();
    } else {
      // Copied error message from the original lazyLoadDelegate
      Log.error("0xA1038 " + ArcComponentTypeSymbolSurrogate.class.getSimpleName() +
        " Could not load full information of '" + name +
        "' (Kind " + "arcbasis._symboltable.ArcComponentTypeSymbol" + ")."
      );
      return this;
    }
  }

  protected Optional<ArcComponentTypeSymbol> tryGeneric() {
    Optional<TypeVarSymbol> resolvedTypeSymbol = this.getEnclosingScope().resolveTypeVar(this.getName());
    if (resolvedTypeSymbol.isPresent()) {
      ArcComponentTypeSymbol resolvedSymbol = this.getEnclosingScope().resolveArcComponentType(resolvedTypeSymbol.get().getSuperTypes(0).printFullName()).orElse(null);
      return Optional.ofNullable(resolvedSymbol);
    }
    return Optional.empty();
  }

  @Override
  public void setSpannedScope(@NotNull IArcBasisScope spannedScope) {
    if (checkLazyLoadDelegate()) {
      this.lazyLoadDelegate().setSpannedScope(spannedScope);
    } else {
      super.setSpannedScope(spannedScope);  // Avoid infinite recursion with this case
    }
  }

  @Override
  public IArcBasisScope getSpannedScope () {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getSpannedScope() :
      super.getSpannedScope();  // Avoid infinite recursion with this case
  }

  @Override
  public boolean isInnerComponent() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().isInnerComponent() :
      super.isInnerComponent();  // Avoid infinite recursion with this case
  }

  @Override
  public Optional<ArcComponentTypeSymbol> getOuterComponent() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getOuterComponent() :
      super.getOuterComponent();  // Avoid infinite recursion with this case
  }

  @Override
  public void setOuterComponent(@Nullable ArcComponentTypeSymbol outerComponent) {
    if (checkLazyLoadDelegate()) {
      this.lazyLoadDelegate().setOuterComponent(outerComponent);
    } else {
      super.setOuterComponent(outerComponent);  // Avoid infinite recursion with this case
    }
  }

  @Override
  public List<VariableSymbol> getParameterList() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getParameterList() :
      super.getParameterList();  // Avoid infinite recursion with this case
  }

  @Override
  public boolean addParameter(@NotNull VariableSymbol parameter) {
    if (checkLazyLoadDelegate()) {
      return this.lazyLoadDelegate().addParameter(parameter);
    } else {
      return super.addParameter(parameter);  // Avoid infinite recursion with this case
    }
  }

  @Override
  public Set<PortSymbol> getAllPorts() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getAllPorts() :
      super.getAllPorts();  // Avoid infinite recursion with this case
  }

  @Override
  public Optional<ASTArcBehaviorElement> getBehavior() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getBehavior() :
      super.getBehavior();  // Avoid infinite recursion with this case
  }
}