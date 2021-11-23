/* (c) https://github.com/MontiCore/monticore */
package arccore.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcDeriveTypeTest;
import arccore.ArcCoreMill;
import arccore._visitor.ArcCoreTraverser;
import de.monticore.types.check.TypeCheckResult;
import montiarc.util.check.IArcDerive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Holds test for methods of {@link ArcCoreDeriveType}.
 *
 * @see AbstractArcDeriveTypeTest for basic tests methods.
 */
public class ArcCoreDeriveTypeTest extends AbstractArcDeriveTypeTest {

  @Override
  @BeforeEach
  public void init() {
    ArcCoreMill.globalScope().clear();
    ArcCoreMill.reset();
    ArcCoreMill.init();
    addBasicTypes2Scope();
    this.setUp();
  }

  @Override
  protected IArcDerive getDerive() {
    if (this.derive == null) {
      this.derive = new ArcCoreDeriveType(new TypeCheckResult());
    }
    return this.derive;
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
    ArcCoreDeriveType typesCalculator = new ArcCoreDeriveType(new TypeCheckResult());

    //When
    ArcCoreTraverser delegator = typesCalculator.getCalculationDelegator();

    //Then
    Assertions.assertTrue(delegator != null);
  }
}