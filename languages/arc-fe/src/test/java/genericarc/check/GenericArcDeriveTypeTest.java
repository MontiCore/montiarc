/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcDeriveTypeTest;
import de.monticore.types.check.TypeCheckResult;
import genericarc.GenericArcMill;
import genericarc._visitor.GenericArcTraverser;
import montiarc.util.check.IArcDerive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Holds test for methods of {@link GenericArcDeriveType}.
 *
 * @see AbstractArcDeriveTypeTest for basic tests methods.
 */
public class GenericArcDeriveTypeTest extends AbstractArcDeriveTypeTest {

  @Override
  @BeforeEach
  public void init() {
    GenericArcMill.globalScope().clear();
    GenericArcMill.reset();
    GenericArcMill.init();
    addBasicTypes2Scope();
    this.setUp();
  }

  @Override
  protected IArcDerive getDerive() {
    if (this.derive == null) {
      this.derive = new GenericArcDeriveType(new TypeCheckResult());
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
    GenericArcDeriveType typesCalculator = new GenericArcDeriveType(new TypeCheckResult());

    //When
    GenericArcTraverser delegator = typesCalculator.getCalculationDelegator();

    //Then
    Assertions.assertTrue(delegator != null);
  }
}
