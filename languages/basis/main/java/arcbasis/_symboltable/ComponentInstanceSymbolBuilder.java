/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTArcArgument;
import arcbasis.check.CompTypeExpression;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

public class ComponentInstanceSymbolBuilder extends ComponentInstanceSymbolBuilderTOP {

  protected CompTypeExpression type;
  protected List<ASTArcArgument> arguments;
  protected List<SymTypeExpression> typeParameters;

  public ComponentInstanceSymbolBuilder() {
    super();
  }

  @Override
  public ComponentInstanceSymbolBuilder setName(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return super.setName(name);
  }

  public CompTypeExpression getType() {
    return this.type;
  }

  public ComponentInstanceSymbolBuilder setType(@NotNull CompTypeExpression type) {
    this.type = Preconditions.checkNotNull(type);
    return this.realBuilder;
  }

  public List<ASTArcArgument> getArcArguments() {
    return this.arguments;
  }

  public ComponentInstanceSymbolBuilder setArcArguments(@NotNull List<ASTArcArgument> arguments) {
    Preconditions.checkNotNull(arguments);
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
    symbol.setType(this.getType());
    if (this.getArcArguments() != null && this.getType() != null) {
      symbol.getType().addArcArguments(this.getArcArguments());
    }
    return symbol;
  }

  @Override
  public boolean isValid() {
    return this.getName() != null;
  }
}