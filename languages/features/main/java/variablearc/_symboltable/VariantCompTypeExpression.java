/* (c) https://github.com/MontiCore/monticore */
package variablearc._symboltable;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.TypeExprOfComponent;
import com.google.common.base.Preconditions;
import org.codehaus.commons.nullanalysis.NotNull;

public class VariantCompTypeExpression extends TypeExprOfComponent {

  public VariantCompTypeExpression(@NotNull ComponentTypeSymbol compTypeSymbol) {
    super(compTypeSymbol);
  }
}
