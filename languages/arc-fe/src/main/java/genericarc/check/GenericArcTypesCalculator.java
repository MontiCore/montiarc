/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import montiarc.util.check.AbstractArcTypesCalculator;
import com.google.common.base.Preconditions;
import de.monticore.types.check.LastResult;
import genericarc.GenericArcMill;
import genericarc._visitor.IGenericArcDelegatorVisitor;
import org.codehaus.commons.nullanalysis.NotNull;
import de.monticore.types.check.SymTypeExpression;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in GenericArc.
 */
public class GenericArcTypesCalculator
  extends AbstractArcTypesCalculator {

  public GenericArcTypesCalculator(@NotNull LastResult typeCheckResult) {
    this(typeCheckResult, GenericArcMill.genericArcDelegatorVisitorBuilder().build());
  }

  protected GenericArcTypesCalculator(@NotNull LastResult typeCheckResult,
                                      @NotNull IGenericArcDelegatorVisitor typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected IGenericArcDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof IGenericArcDelegatorVisitor);
    return (IGenericArcDelegatorVisitor) super.getCalculationDelegator();
  }
}