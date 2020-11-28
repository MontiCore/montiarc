/* (c) https://github.com/MontiCore/monticore */
package montiarc.util.check;

import com.google.common.base.Preconditions;
import de.monticore.types.check.LastResult;
import montiarc.MontiArcMill;
import montiarc._visitor.IMontiArcDelegatorVisitor;
import org.codehaus.commons.nullanalysis.NotNull;

public class MontiArcTypesCalculator extends AbstractArcTypesCalculator {

  public MontiArcTypesCalculator(@NotNull LastResult typeCheckResult) {
    this(typeCheckResult, MontiArcMill.montiArcDelegatorVisitorBuilder().build());
  }

  protected MontiArcTypesCalculator(@NotNull LastResult typeCheckResult,
                                    @NotNull IMontiArcDelegatorVisitor typesCalculator) {
    super(typeCheckResult, typesCalculator);
  }

  @Override
  protected IMontiArcDelegatorVisitor getCalculationDelegator() {
    Preconditions.checkState(super.getCalculationDelegator() instanceof IMontiArcDelegatorVisitor);
    return (IMontiArcDelegatorVisitor) super.getCalculationDelegator();
  }
}