/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTArcArgument;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.List;

public class ComponentTypeSymbolBuilder extends ComponentTypeSymbolBuilderTOP {

  protected ComponentTypeSymbol outerComponent;
  protected List<VariableSymbol> parameters;
  protected List<TypeVarSymbol> typeParameters;
  protected List<ASTArcArgument> parentConfiguration;

  public ComponentTypeSymbolBuilder() {
    super();
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

  public List<ASTArcArgument> getParentConfiguration() {
    return this.parentConfiguration;
  }

  public ComponentTypeSymbolBuilder setParentConfiguration(@NotNull List<ASTArcArgument> parentConfiguration) {
    Preconditions.checkNotNull(parentConfiguration);
    Preconditions.checkArgument(!parentConfiguration.contains(null));
    this.parentConfiguration = parentConfiguration;
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
    if (this.getParentConfiguration() != null) {
      symbol.setParentConfigurationExpressions(this.getParentConfiguration());
    }
    symbol.setOuterComponent(this.getOuterComponent());
    if (this.parent.isPresent()) {
      symbol.setParent(this.parent.get());
    } else {
      symbol.setParentAbsent();
    }
    return symbol;
  }

  @Override
  public boolean isValid() {
    return getName() != null && getSpannedScope() != null;
  }
}
