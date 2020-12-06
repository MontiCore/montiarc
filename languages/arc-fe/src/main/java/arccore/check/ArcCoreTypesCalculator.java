/* (c) https://github.com/MontiCore/monticore */
package arccore.check;

import montiarc.util.check.AbstractArcTypesCalculator;
import arccore.ArcCoreMill;
import arccore._visitor.IArcCoreDelegatorVisitor;
import com.google.common.base.Preconditions;
import de.monticore.types.check.LastResult;
import org.codehaus.commons.nullanalysis.NotNull;
import de.monticore.types.check.SymTypeExpression;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ArcCore.
 */
public class ArcCoreTypesCalculator
  extends AbstractArcTypesCalculator {

  public ArcCoreTypesCalculator(@NotNull LastResult typeCheckResult) {
    this(typeCheckResult, ArcCoreMill.arcCoreDelegatorVisitorBuilder().build());
  }

  protected ArcCoreTypesCalculator(@NotNull LastResult typeCheckResult,
                                   @NotNull IArcCoreDelegatorVisitor typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected IArcCoreDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof IArcCoreDelegatorVisitor);
    return (IArcCoreDelegatorVisitor) super.getCalculationDelegator();
  }
}