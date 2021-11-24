/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcDeriveTypeTest;
import comfortablearc.ComfortableArcMill;
import comfortablearc._visitor.ComfortableArcTraverser;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.TypeCheckResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Holds test for methods of {@link ComfortableArcDeriveType}.
 *
 * @see AbstractArcDeriveTypeTest for basic tests methods.
 */
public class ComfortableArcDeriveTypeTest extends AbstractArcDeriveTypeTest {

  @Override
  @BeforeEach
  public void init() {
    ComfortableArcMill.globalScope().clear();
    ComfortableArcMill.reset();
    ComfortableArcMill.init();
    addBasicTypes2Scope();
    this.setUp();
  }

  @Override
  protected IDerive getDerive() {
    if (this.derive == null) {
      this.derive = new ComfortableArcDeriveType(new TypeCheckResult());
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
    ComfortableArcDeriveType typesCalculator = new ComfortableArcDeriveType(new TypeCheckResult());

    //When
    ComfortableArcTraverser delegator = typesCalculator.getCalculationDelegator();

    //Then
    Assertions.assertTrue(delegator != null);
  }
}