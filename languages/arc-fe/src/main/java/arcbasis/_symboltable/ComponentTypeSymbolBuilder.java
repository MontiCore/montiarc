/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.List;

public class ComponentTypeSymbolBuilder extends ComponentTypeSymbolBuilderTOP {

  protected ComponentTypeSymbol outerComponent;
  protected ComponentTypeSymbolLoader parentComponentLoader;
  protected List<FieldSymbol> parameters;
  protected List<TypeVarSymbol> typeParameters;

  public ComponentTypeSymbolBuilder() {
    super();
  }

  @Override
  public ComponentTypeSymbolBuilder setName(@NotNull String name) {
    Preconditions.checkArgument(name != null);
    return super.setName(name);
  }

  @Override
  public ComponentTypeSymbolBuilder setSpannedScope(@NotNull IArcBasisScope spannedScope) {
    Preconditions.checkArgument(spannedScope != null);
    return super.setSpannedScope(spannedScope);
  }

  public ComponentTypeSymbol getOuterComponent() {
    return this.outerComponent;
  }

  public ComponentTypeSymbolBuilder setOuterComponent(@Nullable ComponentTypeSymbol outerComponent) {
    this.outerComponent = outerComponent;
    return this.realBuilder;
  }

  public ComponentTypeSymbolLoader getParentComponentLoader() {
    return this.parentComponentLoader;
  }

  public ComponentTypeSymbolBuilder setParentComponentLoader(
    @Nullable ComponentTypeSymbolLoader parentComponentLoader) {
    this.parentComponentLoader = parentComponentLoader;
    return this.realBuilder;
  }

  public List<FieldSymbol> getParameters() {
    return this.parameters;
  }

  public ComponentTypeSymbolBuilder setParameters(@NotNull List<FieldSymbol> parameters) {
    Preconditions.checkArgument(parameters != null);
    Preconditions.checkArgument(!parameters.contains(null));
    this.parameters = parameters;
    return this.realBuilder;
  }

  public List<TypeVarSymbol> getTypeParameters() {
    return this.typeParameters;
  }

  public ComponentTypeSymbolBuilder setTypeParameters(@NotNull List<TypeVarSymbol> typeParameters) {
    Preconditions.checkArgument(typeParameters != null);
    Preconditions.checkArgument(!typeParameters.contains(null));
    this.typeParameters = typeParameters;
    return this.realBuilder;
  }

  @Override
  public ComponentTypeSymbol build() {
    if (!isValid()) {
      Preconditions.checkState(this.getName() != null);
      Preconditions.checkState(this.getSpannedScope() != null);
    }
    ComponentTypeSymbol symbol = super.build();
    symbol.setSpannedScope(this.getSpannedScope());
    if (this.getParameters() != null) {
      symbol.addParameters(this.getParameters());
    }
    if (this.getTypeParameters() != null) {
      symbol.addTypeParameters(this.getTypeParameters());
    }
    symbol.setOuterComponent(this.getOuterComponent());
    symbol.setParent(this.getParentComponentLoader());
    return symbol;
  }

  @Override
  public boolean isValid() {
    return getName() != null && getSpannedScope() != null;
  }
}