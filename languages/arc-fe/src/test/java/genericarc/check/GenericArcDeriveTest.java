package genericarc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcDeriveTest;
import de.monticore.types.check.TypeCheckResult;
import genericarc.GenericArcMill;
import genericarc._visitor.GenericArcTraverser;
import montiarc.util.check.IArcDerive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Holds test for methods of {@link GenericArcDerive}.
 *
 * @see AbstractArcDeriveTest for basic tests methods.
 */
public class GenericArcDeriveTest extends AbstractArcDeriveTest {

  @BeforeAll
  public static void initMill() {
    GenericArcMill.init();
  }

  @Override
  protected IArcDerive getDerive() {
    if (this.derive == null) {
      this.derive = new GenericArcDerive(new TypeCheckResult());
    }
    return this.derive;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = GenericArcMill.scope();
    }
    return this.scope;
  }

  @Test
  public void shouldReturnCorrectCalculationDelegator() {
    //Given
    GenericArcDerive typesCalculator = new GenericArcDerive(new TypeCheckResult());

    //When
    GenericArcTraverser delegator = typesCalculator.getCalculationDelegator();

    //Then
    Assertions.assertTrue(delegator != null);
  }
}