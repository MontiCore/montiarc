/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import de.monticore.types.check.*;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcTypeCheck extends TypeCheck {

  public ArcTypeCheck(@NotNull ISynthesize synthesizeSymType, @NotNull IDerive iTypesCalculator) {
    super(Preconditions.checkNotNull(synthesizeSymType), Preconditions.checkNotNull(iTypesCalculator));
  }
}