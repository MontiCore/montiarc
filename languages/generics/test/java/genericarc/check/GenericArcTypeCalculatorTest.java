/* (c) https://github.com/MontiCore/monticore */
package genericarc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcTypeCalculatorTest;
import arcbasis.check.IArcTypeCalculator;
import genericarc.GenericArcAbstractTest;
import genericarc.GenericArcMill;
import org.junit.jupiter.api.BeforeEach;

/**
 * Holds test for methods of {@link GenericArcTypeCalculator}.
 *
 * @see AbstractArcTypeCalculatorTest for basic tests methods.
 */
public class GenericArcTypeCalculatorTest extends AbstractArcTypeCalculatorTest {

  @Override
  @BeforeEach
  public void setUp() {
    GenericArcMill.globalScope().clear();
    GenericArcMill.reset();
    GenericArcMill.init();
    GenericArcAbstractTest.addBasicTypes2Scope();
    this.setUpScope();
  }

  @Override
  protected IArcTypeCalculator getTypeCalculator() {
    if (this.typeCalculator == null) {
      this.typeCalculator = new GenericArcTypeCalculator();
    }
    return this.typeCalculator;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = GenericArcMill.scope();
    }
    return this.scope;
  }
}
