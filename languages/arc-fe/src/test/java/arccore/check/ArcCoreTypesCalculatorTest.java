/* (c) https://github.com/MontiCore/monticore */
package arccore.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcTypesCalculatorTest;
import arccore.ArcCoreMill;
import arccore._visitor.ArcCoreTraverser;
import de.monticore.types.check.TypeCheckResult;
import montiarc.util.check.IArcTypesCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Holds test for methods of {@link ArcCoreTypesCalculator}.
 *
 * @see AbstractArcTypesCalculatorTest for basic tests methods.
 */
public class ArcCoreTypesCalculatorTest extends AbstractArcTypesCalculatorTest {

  @Override
  protected IArcTypesCalculator getTypesCalculator() {
    if (this.typesCalculator == null) {
      this.typesCalculator = new ArcCoreTypesCalculator(new TypeCheckResult());
    }
    return this.typesCalculator;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = ArcCoreMill.scope();
    }
    return this.scope;
  }

  @Test
  public void shouldReturnCorrectCalculationDelegator() {
    //Given
    ArcCoreTypesCalculator typesCalculator = new ArcCoreTypesCalculator(new TypeCheckResult());

    //When
    ArcCoreTraverser delegator = typesCalculator.getCalculationDelegator();

    //Then
    Assertions.assertTrue(delegator != null);
  }
}