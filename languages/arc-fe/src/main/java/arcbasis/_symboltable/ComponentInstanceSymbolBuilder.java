/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

public class ComponentInstanceSymbolBuilder extends ComponentInstanceSymbolBuilderTOP {

  protected ComponentTypeSymbolLoader type;
  protected List<ASTExpression> arguments;

  public ComponentInstanceSymbolBuilder() {
    super();
  }

  @Override
  public ComponentInstanceSymbolBuilder setName(@NotNull String name) {
    Preconditions.checkArgument(name != null);
    return super.setName(name);
  }

  public ComponentTypeSymbolLoader getType() {
    return this.type;
  }

  public ComponentInstanceSymbolBuilder setType(@NotNull ComponentTypeSymbolLoader type) {
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
    symbol.setType(this.getType());
    return symbol;
  }

  @Override
  public boolean isValid() {
    return this.getName() != null && this.getType() != null;
  }
}