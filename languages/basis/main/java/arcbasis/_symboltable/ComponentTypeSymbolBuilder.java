/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComponentTypeSymbolBuilder extends ComponentTypeSymbolBuilderTOP {

  protected ComponentTypeSymbol outerComponent;
  protected List<VariableSymbol> parameters = new ArrayList<>();
  protected List<TypeVarSymbol> typeParameters;

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
    Preconditions.checkArgument(parameters.stream().noneMatch(Objects::isNull));
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

  @Override
  public ComponentTypeSymbol build() {
    Preconditions.checkState(isValid());
    return doBuild(new ComponentTypeSymbol(this.name));
  }

  protected ComponentTypeSymbol doBuild(@NotNull ComponentTypeSymbol symbol) {
    Preconditions.checkNotNull(symbol);
    Preconditions.checkState(isValid());
    symbol.setSuperComponentsList(this.superComponents);
    symbol.setRefinementsList(this.refinements);
    symbol.setName(this.name);
    symbol.setFullName(this.fullName);
    symbol.setPackageName(this.packageName);
    if (this.astNode.isPresent()) {
      symbol.setAstNode(this.astNode.get());
    } else {
      symbol.setAstNodeAbsent();
    }
    symbol.setAccessModifier(this.accessModifier);
    symbol.setEnclosingScope(this.enclosingScope);
    symbol.setSpannedScope(this.spannedScope);
    if (this.parameters != null) {
      this.parameters.forEach(this.getSpannedScope()::add);
      symbol.addParameters(this.parameters);
    }
    symbol.setNumOptParams(this.numOptParams);
    if (this.typeParameters != null) {
      this.getTypeParameters().forEach(symbol.getSpannedScope()::add);
    }
    symbol.setOuterComponent(this.getOuterComponent());
    return symbol;
  }

  @Override
  public boolean isValid() {
    return this.name != null
      && this.spannedScope != null;
     // && isValidNumOptParams();
  }

  protected final boolean isValidNumOptParams() {
    return this.parameters.size() >= this.numOptParams;
  }
}
