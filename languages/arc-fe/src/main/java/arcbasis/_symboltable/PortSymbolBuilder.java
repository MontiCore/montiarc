/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTPortDirection;
import arcbasis._ast.ASTPortDirectionIn;
import arcbasis.ArcBasisMill;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

public class PortSymbolBuilder extends PortSymbolBuilderTOP {

  protected ASTPortDirection direction;
  protected SymTypeExpression type;

  public PortSymbolBuilder() {
    super();
  }

  @Override
  public PortSymbolBuilder setName(@NotNull String name) {
    Preconditions.checkArgument(name != null);
    return super.setName(name);
  }

  public SymTypeExpression getType() {
    return this.type;
  }

  public PortSymbolBuilder setType(@NotNull SymTypeExpression type) {
    Preconditions.checkArgument(type != null);
    this.type = type;
    return this.realBuilder;
  }

  public ASTPortDirection getDirection() {
    return this.direction;
  }

  public PortSymbolBuilder setDirection(@NotNull ASTPortDirection direction) {
    Preconditions.checkArgument(direction != null);
    this.direction = direction;
    return this.realBuilder;
  }

  public boolean isIncoming() {
    return this.getDirection() instanceof ASTPortDirectionIn;
  }

  public PortSymbolBuilder setIncoming(boolean incoming) {
    if (incoming) {
      this.setDirection(ArcBasisMill.portDirectionInBuilder().build());
    } else {
      this.setDirection(ArcBasisMill.portDirectionOutBuilder().build());
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