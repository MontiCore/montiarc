/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis._ast.ASTPortDirection;
import arcbasis._ast.ASTPortDirectionIn;
import arcbasis.ArcBasisMill;
import arcbasis.timing.Timing;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class PortSymbolBuilder extends PortSymbolBuilderTOP {

  protected ASTPortDirection direction;
  protected SymTypeExpression type;
  protected Timing timing;

  public PortSymbolBuilder() {
    super();
  }

  @Override
  public PortSymbolBuilder setName(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return super.setName(name);
  }

  public SymTypeExpression getType() {
    return this.type;
  }

  public PortSymbolBuilder setType(@NotNull SymTypeExpression type) {
    Preconditions.checkNotNull(type);
    this.type = type;
    return this.realBuilder;
  }

  public ASTPortDirection getDirection() {
    return this.direction;
  }

  public PortSymbolBuilder setDirection(@NotNull ASTPortDirection direction) {
    Preconditions.checkNotNull(direction);
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

  public Timing getTiming() {
    Preconditions.checkState(this.timing != null, "Type of Port '%s' has not been set. Did you " +
        "forget to run the symbol table completer?", this.getName());
    return this.timing;
  }

  public PortSymbolBuilder setTiming(@NotNull Timing timing) {
    Preconditions.checkNotNull(timing);
    this.timing = timing;
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
    symbol.setTiming(Optional.ofNullable(this.timing).orElse(Timing.UNTIMED));
    return symbol;
  }

  public PortSymbol buildWithoutType() {
    if (!isValidWithoutType()) {
      Preconditions.checkState(this.getName() != null);
      Preconditions.checkState(this.getDirection() != null);
    }
    PortSymbol symbol = super.build();
    symbol.setDirection(this.getDirection());
    symbol.setTiming(Optional.ofNullable(this.timing).orElse(Timing.UNTIMED));
    return symbol;
  }

  @Override
  public boolean isValid() {
    return this.getName() != null && this.getType() != null && this.getDirection() !=null;
  }

  public boolean isValidWithoutType() {
    return this.getName() != null && this.getDirection() != null;
  }
}