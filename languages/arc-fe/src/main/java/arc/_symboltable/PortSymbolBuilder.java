/* (c) https://github.com/MontiCore/monticore */
package arc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;
import org.codehaus.commons.nullanalysis.NotNull;

public class PortSymbolBuilder extends PortSymbolBuilderTOP {

  protected String direction;
  protected TypeSymbolLoader type;

  protected PortSymbolBuilder() {
    super();
  }

  @Override
  public PortSymbolBuilder setName(@NotNull String name) {
    Preconditions.checkArgument(name != null);
    return super.setName(name);
  }

  public TypeSymbolLoader getType() {
    return this.type;
  }

  public PortSymbolBuilder setType(@NotNull TypeSymbolLoader type) {
    Preconditions.checkArgument(type != null);
    this.type = type;
    return this.realBuilder;
  }

  public String getDirection() {
    return this.direction;
  }

  public PortSymbolBuilder setDirection(@NotNull String direction) {
    Preconditions.checkArgument(direction != null);
    this.direction = direction;
    return this.realBuilder;
  }

  public boolean isIncoming() {
    return this.getDirection().equals("in");
  }

  public PortSymbolBuilder setIncoming(boolean incoming) {
    if (incoming) {
      this.setDirection("in");
    } else {
      this.setDirection("out");
    }
    return this.realBuilder;
  }

  @Override
  public PortSymbol build() {
    if (!isValid()) {
      Preconditions.checkState(this.getName() != null);
      Preconditions.checkState(this.getType() != null);
      Preconditions.checkState(this.getDirection() != null);
    }
    PortSymbol symbol = super.build();
    symbol.setDirection(this.getDirection());
    symbol.setType(this.getType());
    return symbol;
  }

  @Override
  public boolean isValid() {
    return this.getName() != null && this.getType() != null && this.getDirection() !=null;
  }
}