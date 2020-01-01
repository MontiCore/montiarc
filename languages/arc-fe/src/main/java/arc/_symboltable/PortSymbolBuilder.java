/* (c) https://github.com/MontiCore/monticore */
package arc._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.types.typesymbols._symboltable.TypeSymbolLoader;
import org.codehaus.commons.nullanalysis.NotNull;

public class PortSymbolBuilder extends PortSymbolBuilderTOP {

  protected boolean incoming;
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

  public boolean isIncoming() {
    return this.incoming;
  }

  public PortSymbolBuilder setIncoming(boolean incoming) {
    this.incoming = incoming;
    return this.realBuilder;
  }

  @Override
  public PortSymbol build() {
    if (!isValid()) {
      Preconditions.checkState(this.getName() != null);
      Preconditions.checkState(this.getType() != null);
    }
    PortSymbol symbol = super.build();
    symbol.setDirection(incoming);
    symbol.setType(this.getType());
    return symbol;
  }

  @Override
  public boolean isValid() {
    return this.getName() != null && this.getType() != null;
  }
}