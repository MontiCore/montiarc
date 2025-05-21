/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolSurrogate;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.List;

public class ArcComponentTypeSymbolBuilder extends ArcComponentTypeSymbolBuilderTOP {

  protected ArcComponentTypeSymbol outerComponent;
  protected List<TypeVarSymbol> typeParameters;

  public ArcComponentTypeSymbolBuilder() {
    super();
  }

  @Override
  public ArcComponentTypeSymbolBuilder setName(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return super.setName(name);
  }

  @Override
  public ArcComponentTypeSymbolBuilder setSpannedScope(@NotNull IArcBasisScope spannedScope) {
    Preconditions.checkNotNull(spannedScope);
    return super.setSpannedScope(spannedScope);
  }

  public ArcComponentTypeSymbol getOuterComponent() {
    return this.outerComponent;
  }

  public ArcComponentTypeSymbolBuilder setOuterComponent(@Nullable ArcComponentTypeSymbol outerComponent) {
    Preconditions.checkArgument(!(outerComponent instanceof ArcComponentTypeSymbolSurrogate));
    this.outerComponent = outerComponent;
    return this.realBuilder;
  }

  public List<TypeVarSymbol> getTypeParameters() {
    return this.typeParameters;
  }

  public ArcComponentTypeSymbolBuilder setTypeParameters(@NotNull List<TypeVarSymbol> typeParameters) {
    Preconditions.checkNotNull(typeParameters);
    Preconditions.checkArgument(!typeParameters.contains(null));
    this.typeParameters = typeParameters;
    return this.realBuilder;
  }

  @Override
  public ArcComponentTypeSymbol build() {
    Preconditions.checkState(isValid());
    return doBuild(new ArcComponentTypeSymbol(this.name));
  }

  protected ArcComponentTypeSymbol doBuild(@NotNull ArcComponentTypeSymbol symbol) {
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
    if (this.parameter != null) {
      this.parameter.forEach(this.getSpannedScope()::add);
      symbol.addParameters(this.parameter);
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
    return this.parameter.size() >= this.numOptParams;
  }
}
