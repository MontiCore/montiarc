/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcTypesCalculatorTest;
import comfortablearc.ComfortableArcMill;
import comfortablearc._visitor.ComfortableArcDelegatorVisitor;
import comfortablearc._visitor.IComfortableArcDelegatorVisitor;
import de.monticore.types.check.LastResult;
import montiarc.util.check.IArcTypesCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Holds test for methods of {@link ComfortableArcTypesCalculator}.
 *
 * @see AbstractArcTypesCalculatorTest for basic tests methods.
 */
public class ComfortableArcTypesCalculatorTest extends AbstractArcTypesCalculatorTest {

  @Override
  protected IArcTypesCalculator getTypesCalculator() {
    if (this.typesCalculator == null) {
      this.typesCalculator = new ComfortableArcTypesCalculator(new LastResult());
    }
    return this.typesCalculator;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = ComfortableArcMill.comfortableArcScopeBuilder().build();
    }
    return this.scope;
  }

  @Test
  public void shouldReturnCorrectCalculationDelegator() {
    //Given
    ComfortableArcTypesCalculator typesCalculator = new ComfortableArcTypesCalculator(new LastResult());

    //When
    IComfortableArcDelegatorVisitor delegator = typesCalculator.getCalculationDelegator();

    //Then
    Assertions.assertTrue(delegator instanceof ComfortableArcDelegatorVisitor);
  }
}