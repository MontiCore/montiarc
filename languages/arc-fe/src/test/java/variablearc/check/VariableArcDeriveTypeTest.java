/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcDeriveTypeTest;
import arcbasis.check.ArcBasisDeriveType;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.TypeCheckResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import variablearc.VariableArcMill;
import variablearc._visitor.VariableArcTraverser;
import variablearc.check.VariableArcDeriveType;

/**
 * Holds test for methods of {@link ArcBasisDeriveType}.
 *
 * @see AbstractArcDeriveTypeTest for basic tests methods.
 */
public class VariableArcDeriveTypeTest extends AbstractArcDeriveTypeTest {

  @Override
  @BeforeEach
  public void init() {
    VariableArcMill.globalScope().clear();
    VariableArcMill.reset();
    VariableArcMill.init();
    addBasicTypes2Scope();
    this.setUp();
  }

  @Override
  protected IDerive getDerive() {
    if (this.derive == null) {
      this.derive = new VariableArcDeriveType(new TypeCheckResult());
    }
    return this.derive;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = VariableArcMill.scope();
    }
    return this.scope;
  }

  @Test
  public void shouldReturnCorrectCalculationDelegator() {
    //Given
    VariableArcDeriveType typesCalculator = new VariableArcDeriveType(new TypeCheckResult());

    //When
    VariableArcTraverser delegator = typesCalculator.getCalculationDelegator();

    //Then
    Assertions.assertNotNull(delegator);
  }
}
