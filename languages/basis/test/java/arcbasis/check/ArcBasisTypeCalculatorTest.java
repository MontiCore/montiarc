/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.IArcBasisScope;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;

/**
 * Holds test for methods of {@link ArcBasisTypeCalculator}.
 *
 * @see AbstractArcTypeCalculatorTest for basic tests methods.
 */
public class ArcBasisTypeCalculatorTest extends AbstractArcTypeCalculatorTest {

  @Override
  @BeforeEach
  public void setUp() {
    Log.clearFindings();
    ArcBasisMill.globalScope().clear();
    ArcBasisMill.reset();
    ArcBasisMill.init();
    addBasicTypes2Scope();
    this.setUpScope();
  }

  @Override
  protected IArcTypeCalculator getTypeCalculator() {
    if (this.typeCalculator == null) {
      this.typeCalculator = new ArcBasisTypeCalculator(new TypeCheckResult());
    }
    return this.typeCalculator;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = ArcBasisMill.scope();
    }
    return this.scope;
  }
}