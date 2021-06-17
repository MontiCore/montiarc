/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import de.monticore.types.check.*;
import org.codehaus.commons.nullanalysis.NotNull;

public class ArcTypeCheck extends TypeCheck {

  public ArcTypeCheck(@NotNull ISynthesize synthesizeSymType, @NotNull IDerive iTypesCalculator) {
    super(Preconditions.checkNotNull(synthesizeSymType), Preconditions.checkNotNull(iTypesCalculator));
  }

  //TODO: Remove once fixed in MontiCore.
  // We just call MontiCore's TypeCheck, but we "clean" the SymTypeExpressions from surrogates and we also force
  // MontiCore's type check to compare fully qualified names instead of simple names within the TypeCheck.
  public static boolean compatible(SymTypeExpression left, SymTypeExpression right) {
    return TypeCheck.compatible(AdaptedSymType.of(left), AdaptedSymType.of(right));
  }
}