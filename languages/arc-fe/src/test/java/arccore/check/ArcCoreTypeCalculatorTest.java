/* (c) https://github.com/MontiCore/monticore */
package arccore.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcTypeCalculatorTest;
import arcbasis.check.IArcTypeCalculator;
import arccore.ArcCoreMill;
import de.monticore.types.check.TypeCheckResult;
import org.junit.jupiter.api.BeforeEach;

/**
 * Holds test for methods of {@link ArcCoreTypeCalculator}.
 *
 * @see AbstractArcTypeCalculatorTest for basic tests methods.
 */
public class ArcCoreTypeCalculatorTest extends AbstractArcTypeCalculatorTest {

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
  protected IArcTypeCalculator getTypeCalculator() {
    if (this.typeCalculator == null) {
      this.typeCalculator = new ArcCoreTypeCalculator(new TypeCheckResult());
    }
    return this.typeCalculator;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = ArcCoreMill.scope();
    }
    return this.scope;
  }
}