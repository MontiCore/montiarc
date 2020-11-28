/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.check;

import montiarc.util.check.AbstractArcTypesCalculator;
import com.google.common.base.Preconditions;
import comfortablearc.ComfortableArcMill;
import comfortablearc._visitor.IComfortableArcDelegatorVisitor;
import de.monticore.types.check.LastResult;
import org.codehaus.commons.nullanalysis.NotNull;

import de.monticore.types.check.SymTypeExpression;

/**
 * A visitor that calculates a {@link SymTypeExpression} (type) for expressions
 * in ComfortableArc.
 */
public class ComfortableArcTypesCalculator
  extends AbstractArcTypesCalculator {

  public ComfortableArcTypesCalculator(@NotNull LastResult typeCheckResult) {
    this(typeCheckResult, ComfortableArcMill.comfortableArcDelegatorVisitorBuilder().build());
  }

  protected ComfortableArcTypesCalculator(@NotNull LastResult typeCheckResult,
                                          @NotNull IComfortableArcDelegatorVisitor calculationDelegator) {
    super(typeCheckResult, calculationDelegator);
  }

  @Override
  protected IComfortableArcDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof IComfortableArcDelegatorVisitor);
    return (IComfortableArcDelegatorVisitor) super.getCalculationDelegator();
  }
}