/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.SymTypeRelations;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * Adapts the outdated {@link TypeCalculator} to the type check 3. Provided
 * for backwards compatibility with old context-conditions. If possible, use
 * the type check 3 instead. This class will be scheduled for deletion once
 *
 * @deprecated use {@link IArcTypeCalculator}, {@link SymTypeRelations}
 */
@Deprecated
public class TypeCheck3AsTypeCalculator extends TypeCalculator {

  private final SymTypeRelations tr;

  public TypeCheck3AsTypeCalculator(@NotNull IArcTypeCalculator tc,
                                    @NotNull SymTypeRelations tr) {
    super(tc, tc);
    this.tr = Preconditions.checkNotNull(tr);
  }

  @Override
  public boolean compatible(@NotNull SymTypeExpression left,
                            @NotNull SymTypeExpression right) {
    Preconditions.checkNotNull(left);
    Preconditions.checkNotNull(right);
    return this.tr.isCompatible(left, right);
  }

  @Override
  public boolean isSubtypeOf(@NotNull SymTypeExpression subType,
                             @NotNull SymTypeExpression superType) {
    Preconditions.checkNotNull(subType);
    Preconditions.checkNotNull(superType);
    return this.tr.isSubTypeOf(subType, superType);
  }
}
