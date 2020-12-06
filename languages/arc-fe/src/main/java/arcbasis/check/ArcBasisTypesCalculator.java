/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis._visitor.ArcBasisDelegatorVisitor;
import arcbasis._visitor.IArcBasisDelegatorVisitor;
import com.google.common.base.Preconditions;
import de.monticore.types.check.LastResult;
import de.monticore.types.check.SymTypeExpression;
import montiarc.util.check.AbstractArcTypesCalculator;
import org.codehaus.commons.nullanalysis.NotNull;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ArcBasis.
 */
public class ArcBasisTypesCalculator
  extends AbstractArcTypesCalculator {

  public ArcBasisTypesCalculator(@NotNull LastResult typeCheckResult) {
    this(typeCheckResult, new ArcBasisDelegatorVisitor());
  }

  protected ArcBasisTypesCalculator(@NotNull LastResult typeCheckResult,
                                    @NotNull IArcBasisDelegatorVisitor typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected IArcBasisDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof IArcBasisDelegatorVisitor);
    return (IArcBasisDelegatorVisitor) super.getCalculationDelegator();
  }
}