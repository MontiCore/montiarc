/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcDeriveTest;
import comfortablearc.ComfortableArcMill;
import comfortablearc._visitor.ComfortableArcTraverser;
import de.monticore.types.check.TypeCheckResult;
import montiarc.util.check.IArcDerive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Holds test for methods of {@link ComfortableArcDerive}.
 *
 * @see AbstractArcDeriveTest for basic tests methods.
 */
public class ComfortableArcDeriveTest extends AbstractArcDeriveTest {

  @BeforeAll
  public static void initMill() {
    ComfortableArcMill.init();
  }

  @Override
  protected IArcDerive getDerive() {
    if (this.derive == null) {
      this.derive = new ComfortableArcDerive(new TypeCheckResult());
    }
    return this.derive;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = ComfortableArcMill.scope();
    }
    return this.scope;
  }

  @Test
  public void shouldReturnCorrectCalculationDelegator() {
    //Given
    ComfortableArcDerive typesCalculator = new ComfortableArcDerive(new TypeCheckResult());

    //When
    ComfortableArcTraverser delegator = typesCalculator.getCalculationDelegator();

    //Then
    Assertions.assertTrue(delegator != null);
  }
}