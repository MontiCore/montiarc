/* (c) https://github.com/MontiCore/monticore */
package arcbasis._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcPortSymbolBuilder extends ArcPortSymbolBuilderTOP {

  @Override
  public ArcPortSymbolBuilder setName(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return super.setName(name);
  }

  public ArcPortSymbolBuilder setType(@NotNull SymTypeExpression type) {
    Preconditions.checkNotNull(type);
    this.type = type;
    return this.realBuilder;
  }

  @Override
  public ArcPortSymbol build() {
    Preconditions.checkState(this.isValid());
    return super.build();
  }

  public ArcPortSymbol buildWithoutType() {
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