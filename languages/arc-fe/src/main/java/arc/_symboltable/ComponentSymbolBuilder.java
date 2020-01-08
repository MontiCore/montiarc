/* (c) https://github.com/MontiCore/monticore */
package arc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.List;

public class ComponentSymbolBuilder extends ComponentSymbolBuilderTOP {

  protected ComponentSymbol outerComponent;
  protected ComponentSymbolLoader parentComponent;
  protected List<FieldSymbol> parameters;
  protected List<TypeVarSymbol> typeParameters;

  protected ComponentSymbolBuilder() {
    super();
  }

  @Override
  public ComponentSymbolBuilder setName(@NotNull String name) {
    Preconditions.checkArgument(name != null);
    return super.setName(name);
  }

  @Override
  public ComponentSymbolBuilder setSpannedScope(@NotNull IArcScope spannedScope) {
    Preconditions.checkArgument(spannedScope != null);
    return super.setSpannedScope(spannedScope);
  }

  public ComponentSymbol getOuterComponent() {
    return this.outerComponent;
  }

  public ComponentSymbolBuilder setOuterComponent(@Nullable ComponentSymbol outerComponent) {
    this.outerComponent = outerComponent;
    return this.realBuilder;
  }

  public ComponentSymbolLoader getParentComponent() {
    return this.parentComponent;
  }

  public ComponentSymbolBuilder setParentComponent(
    @Nullable ComponentSymbolLoader parentComponent) {
    this.parentComponent = parentComponent;
    return this.realBuilder;
  }

  public List<FieldSymbol> getParameters() {
    return this.parameters;
  }

  public ComponentSymbolBuilder setParameters(@NotNull List<FieldSymbol> parameters) {
    Preconditions.checkArgument(parameters != null);
    Preconditions.checkArgument(!parameters.contains(null));
    this.parameters = parameters;
    return this.realBuilder;
  }

  public List<TypeVarSymbol> getTypeParameters() {
    return this.typeParameters;
  }

  public ComponentSymbolBuilder setTypeParameters(@NotNull List<TypeVarSymbol> typeParameters) {
    Preconditions.checkArgument(typeParameters != null);
    Preconditions.checkArgument(!typeParameters.contains(null));
    this.typeParameters = typeParameters;
    return this.realBuilder;
  }

  @Override
  public ComponentSymbol build() {
    if (!isValid()) {
      Preconditions.checkState(this.getName() != null);
      Preconditions.checkState(this.getSpannedScope() != null);
    }
    ComponentSymbol symbol = super.build();
    symbol.setSpannedScope(this.getSpannedScope());
    if (this.getParameters() != null) {
      symbol.addParameters(this.getParameters());
    }
    if (this.getTypeParameters() != null) {
      symbol.addTypeParameters(this.getTypeParameters());
    }
    symbol.setOuterComponent(this.getOuterComponent());
    symbol.setParentComponent(this.getParentComponent());
    return symbol;
  }

  @Override
  public boolean isValid() {
    return getName() != null && getSpannedScope() != null;
  }
}