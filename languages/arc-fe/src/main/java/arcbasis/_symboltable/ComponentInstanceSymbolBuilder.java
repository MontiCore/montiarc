/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

public class ComponentInstanceSymbolBuilder extends ComponentInstanceSymbolBuilderTOP {

  protected CompTypeExpression type;
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

  public CompTypeExpression getType() {
    return this.type;
  }

  public ComponentInstanceSymbolBuilder setType(@NotNull CompTypeExpression type) {
    this.type = Preconditions.checkNotNull(type);
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
    return this.getName() != null;
  }
}