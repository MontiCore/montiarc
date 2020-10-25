package genericarc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcTypesCalculatorTest;
import de.monticore.types.check.TypeCheckResult;
import genericarc.GenericArcMill;
import genericarc._visitor.GenericArcDelegatorVisitor;
import genericarc._visitor.IGenericArcDelegatorVisitor;
import montiarc.util.check.IArcTypesCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Holds test for methods of {@link GenericArcTypesCalculator}.
 *
 * @see AbstractArcTypesCalculatorTest for basic tests methods.
 */
public class GenericArcTypesCalculatorTest extends AbstractArcTypesCalculatorTest {

  @Override
  protected IArcTypesCalculator getTypesCalculator() {
    if (this.typesCalculator == null) {
      this.typesCalculator = new GenericArcTypesCalculator(new TypeCheckResult());
    }
    return this.typesCalculator;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = GenericArcMill.genericArcScopeBuilder().build();
    }
    return this.scope;
  }

  @Test
  public void shouldReturnCorrectCalculationDelegator() {
    //Given
    GenericArcTypesCalculator typesCalculator = new GenericArcTypesCalculator(new TypeCheckResult());

    //When
    IGenericArcDelegatorVisitor delegator = typesCalculator.getCalculationDelegator();

    //Then
    Assertions.assertTrue(delegator instanceof GenericArcDelegatorVisitor);
  }
}