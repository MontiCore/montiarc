/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import arcbasis.timing.Timing;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Optional;

public class PortSymbolBuilder extends PortSymbolBuilderTOP {

  protected Timing timing;

  public PortSymbolBuilder() {
    super();
    this.realBuilder.incoming = false;
    this.realBuilder.outgoing  = false;
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
    Preconditions.checkState(this.isValid());
    PortSymbol symbol = super.build();
    symbol.setTiming(Optional.ofNullable(this.timing).orElse(Timing.UNTIMED));
    return symbol;
  }

  public PortSymbol buildWithoutType() {
    Preconditions.checkState(this.isValidWithoutType());
    this.setType(SymTypeExpressionFactory.createObscureType());
    return this.build();
  }

  @Override
  public boolean isValid() {
    return this.getName() != null && this.getType() != null;
  }

  public boolean isValidWithoutType() {
    return this.getName() != null;
  }
}