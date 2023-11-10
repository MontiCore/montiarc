/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.ArcBasisMill;
import arcbasis._ast.ASTArcArgument;
import arcbasis._ast.ASTArcBehaviorElement;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.logging.Log;
import de.monticore.symbols.compsymbols._symboltable.Timing;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.List;
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

  @Override
  public ComponentTypeSymbol lazyLoadDelegate() {
    if (this.getDelegate().isEmpty()) {
      this.setDelegate(this.getEnclosingScope().resolveComponentType(this.getName()).orElse(tryGeneric().orElse(null)));
    }

    if (this.getDelegate().isPresent()) {
      return this.getDelegate().get();
    } else {
      // Copied error message from the original lazyLoadDelegate
      Log.error("0xA1038 " + ComponentTypeSymbolSurrogate.class.getSimpleName() +
        " Could not load full information of '" + name +
        "' (Kind " + "arcbasis._symboltable.ComponentTypeSymbol" + ")."
      );
      return this;
    }
  }

  protected Optional<ComponentTypeSymbol> tryGeneric() {
    Optional<TypeVarSymbol> resolvedTypeSymbol = this.getEnclosingScope().resolveTypeVar(this.getName());
    if (resolvedTypeSymbol.isPresent()) {
      ComponentTypeSymbol resolvedSymbol = this.getEnclosingScope().resolveComponentType(resolvedTypeSymbol.get().getSuperTypes(0).printFullName()).orElse(null);
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
  public Optional<ComponentTypeSymbol> getOuterComponent() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getOuterComponent() :
      super.getOuterComponent();  // Avoid infinite recursion with this case
  }

  @Override
  public void setOuterComponent(@Nullable ComponentTypeSymbol outerComponent) {
    if (checkLazyLoadDelegate()) {
      this.lazyLoadDelegate().setOuterComponent(outerComponent);
    } else {
      super.setOuterComponent(outerComponent);  // Avoid infinite recursion with this case
    }
  }

  @Override
  public List<ASTArcArgument> getParentConfiguration(@NotNull CompKindExpression parent) {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getParentConfiguration(parent) :
      super.getParentConfiguration(parent);  // Avoid infinite recursion with this case
  }

  @Override
  public void setParentConfigurationExpressions(@NotNull CompKindExpression parent, @NotNull List<ASTArcArgument> expressions) {
    if (checkLazyLoadDelegate()) {
      this.lazyLoadDelegate().setParentConfigurationExpressions(parent, expressions);
    } else {
      super.setParentConfigurationExpressions(parent, expressions);  // Avoid infinite recursion with this case
    }
  }

  @Override
  public List<VariableSymbol> getParametersList() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getParametersList() :
      super.getParametersList();  // Avoid infinite recursion with this case
  }

  @Override
  public void addParameter(@NotNull VariableSymbol parameter) {
    if (checkLazyLoadDelegate()) {
      this.lazyLoadDelegate().addParameter(parameter);
    } else {
      super.addParameter(parameter);  // Avoid infinite recursion with this case
    }
  }

  @Override
  public Optional<Timing> getTiming() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getTiming() :
      super.getTiming();  // Avoid infinite recursion with this case
  }

  @Override
  public List<ArcPortSymbol> getAllArcPorts() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getAllArcPorts() :
      super.getAllArcPorts();  // Avoid infinite recursion with this case
  }

  @Override
  protected Optional<ASTArcBehaviorElement> getBehavior() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getBehavior() :
      super.getBehavior();  // Avoid infinite recursion with this case
  }
}