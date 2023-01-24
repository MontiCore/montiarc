/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import montiarc.Timing;
import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;
import org.codehaus.commons.nullanalysis.Nullable;

import java.util.Optional;

public class PortSymbolBuilder extends PortSymbolBuilderTOP {

  protected Timing timing;
  protected Boolean delayed;

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

  public PortSymbolBuilder setTiming(@Nullable Timing timing) {
    this.timing = timing;
    return this.realBuilder;
  }

  public PortSymbolBuilder setDelayed(@Nullable Boolean delayed) {
    this.delayed = delayed;
    return this.realBuilder;
  }

  @Override
  public PortSymbol build() {
    Preconditions.checkState(this.isValid());
    PortSymbol symbol = super.build();
    symbol.setTiming(this.timing);
    symbol.setDelayed(this.delayed);
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