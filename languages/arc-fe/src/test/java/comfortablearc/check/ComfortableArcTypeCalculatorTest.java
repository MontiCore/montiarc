/* (c) https://github.com/MontiCore/monticore */
package comfortablearc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcTypeCalculatorTest;
import arcbasis.check.IArcTypeCalculator;
import comfortablearc.ComfortableArcMill;
import de.monticore.types.check.TypeCheckResult;
import org.junit.jupiter.api.BeforeEach;

/**
 * Holds test for methods of {@link ComfortableArcTypeCalculator}.
 *
 * @see AbstractArcTypeCalculatorTest for basic tests methods.
 */
public class ComfortableArcTypeCalculatorTest extends AbstractArcTypeCalculatorTest {

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
  protected IArcTypeCalculator getTypeCalculator() {
    if (this.typeCalculator == null) {
      this.typeCalculator = new ComfortableArcTypeCalculator(new TypeCheckResult());
    }
    return this.typeCalculator;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = ComfortableArcMill.scope();
    }
    return this.scope;
  }
}