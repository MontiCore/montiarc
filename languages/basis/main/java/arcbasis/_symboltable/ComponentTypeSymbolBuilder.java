/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTArcArgument;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentTypeSymbolBuilder extends ComponentTypeSymbolBuilderTOP {

  protected ComponentTypeSymbol outerComponent;
  protected List<VariableSymbol> parameters;
  protected List<TypeVarSymbol> typeParameters;
  protected Map<CompTypeExpression, List<ASTArcArgument>> parentConfiguration;

  public ComponentTypeSymbolBuilder() {
    super();
    parentConfiguration = new HashMap<>();
  }

  @Override
  public ComponentTypeSymbolBuilder setName(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return super.setName(name);
  }

  @Override
  public ComponentTypeSymbolBuilder setSpannedScope(@NotNull IArcBasisScope spannedScope) {
    Preconditions.checkNotNull(spannedScope);
    return super.setSpannedScope(spannedScope);
  }

  public ComponentTypeSymbol getOuterComponent() {
    return this.outerComponent;
  }

  public ComponentTypeSymbolBuilder setOuterComponent(@Nullable ComponentTypeSymbol outerComponent) {
    Preconditions.checkArgument(!(outerComponent instanceof ComponentTypeSymbolSurrogate));
    this.outerComponent = outerComponent;
    return this.realBuilder;
  }

  public List<VariableSymbol> getParameters() {
    return this.parameters;
  }

  public ComponentTypeSymbolBuilder setParameters(@NotNull List<VariableSymbol> parameters) {
    Preconditions.checkNotNull(parameters);
    Preconditions.checkArgument(!parameters.contains(null));
    this.parameters = parameters;
    return this.realBuilder;
  }

  public List<TypeVarSymbol> getTypeParameters() {
    return this.typeParameters;
  }

  public ComponentTypeSymbolBuilder setTypeParameters(@NotNull List<TypeVarSymbol> typeParameters) {
    Preconditions.checkNotNull(typeParameters);
    Preconditions.checkArgument(!typeParameters.contains(null));
    this.typeParameters = typeParameters;
    return this.realBuilder;
  }

  public List<ASTArcArgument> getParentConfiguration(@NotNull CompTypeExpression parent) {
    return this.parentConfiguration.get(parent);
  }

  public ComponentTypeSymbolBuilder setParentConfiguration(@NotNull CompTypeExpression parent, @NotNull List<ASTArcArgument> parentConfiguration) {
    Preconditions.checkNotNull(parentConfiguration);
    Preconditions.checkArgument(!parentConfiguration.contains(null));
    this.parentConfiguration.put(parent, parentConfiguration);
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
      this.getParameters().forEach(symbol.getSpannedScope()::add);
      symbol.addParameters(this.getParameters());
    }
    if (this.getTypeParameters() != null) {
      this.getTypeParameters().forEach(symbol.getSpannedScope()::add);
    }
    symbol.setParentConfigurationMap(this.parentConfiguration);
    symbol.setOuterComponent(this.getOuterComponent());
    symbol.setParentsList(this.parents);
    return symbol;
  }

  @Override
  public boolean isValid() {
    return getName() != null && getSpannedScope() != null;
  }
}
