/* (c) https://github.com/MontiCore/monticore */
package arccore.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcDeriveTest;
import arccore.ArcCoreMill;
import arccore._visitor.ArcCoreTraverser;
import de.monticore.types.check.TypeCheckResult;
import montiarc.util.check.IArcDerive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Holds test for methods of {@link ArcCoreDerive}.
 *
 * @see AbstractArcDeriveTest for basic tests methods.
 */
public class ArcCoreDeriveTest extends AbstractArcDeriveTest {

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
      this.derive = new ArcCoreDerive(new TypeCheckResult());
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
    ArcCoreDerive typesCalculator = new ArcCoreDerive(new TypeCheckResult());

    //When
    ArcCoreTraverser delegator = typesCalculator.getCalculationDelegator();

    //Then
    Assertions.assertTrue(delegator != null);
  }
}