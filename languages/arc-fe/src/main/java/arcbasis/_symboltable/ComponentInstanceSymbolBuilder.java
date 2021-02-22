/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ComponentInstanceSymbolBuilder extends ComponentInstanceSymbolBuilderTOP {

  protected ComponentTypeSymbol type;
  protected List<ASTExpression> arguments;
  protected List<SymTypeExpression> typeParameters;

  public ComponentInstanceSymbolBuilder() {
    super();
  }

  @Override
  public ComponentInstanceSymbolBuilder setName(@NotNull String name) {
    Preconditions.checkArgument(name != null);
    return super.setName(name);
  }

  public ComponentTypeSymbol getType() {
    return this.type;
  }

  public ComponentInstanceSymbolBuilder setType(@NotNull ComponentTypeSymbol type) {
    Preconditions.checkArgument(type != null);
    this.type = type;
    return this.realBuilder;
  }

  public List<ASTExpression> getArguments() {
    return this.arguments;
  }

  public ComponentInstanceSymbolBuilder setArguments(@NotNull List<ASTExpression> arguments) {
    Preconditions.checkArgument(arguments != null);
    Preconditions.checkArgument(!arguments.contains(null));
    this.arguments = arguments;
    return this.realBuilder;
  }

  /**
   * ideally this would belong to generic arc
   * @param typeExpression type expression that may contain generic subtypes
   */
  public void fetchParametersFrom(@NotNull SymTypeExpression typeExpression) {
    Preconditions.checkNotNull(typeExpression);
    if(typeExpression.isGenericType()){
      typeParameters = ((SymTypeOfGenerics) typeExpression).getArgumentList();
    }
  }

  @Override
  public ComponentInstanceSymbol build() {
    if (!isValid()) {
      Preconditions.checkState(this.getName() != null);
      Preconditions.checkState(this.getType() != null);
    }
    ComponentInstanceSymbol symbol = super.build();
    if (this.getArguments() != null) {
      symbol.addArguments(this.getArguments());
    }
    symbol.setTypeParameters(Optional.ofNullable(typeParameters).orElseGet(ArrayList::new));
    symbol.setType(this.getType());
    return symbol;
  }

  @Override
  public boolean isValid() {
    return this.getName() != null && this.getType() != null;
  }
}