/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;

public class PortSymbolBuilder extends PortSymbolBuilderTOP {

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

  @Override
  public PortSymbol build() {
    Preconditions.checkState(this.isValid());
    return super.build();
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