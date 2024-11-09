/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.IArcBasisScope;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;

/**
 * Holds test for methods of {@link ArcBasisTypeCheck}.
 *
 * @see AbstractArcTypeCalculatorTest for basic tests methods.
 */
public class ArcBasisTypeCheckTest extends AbstractArcTypeCalculatorTest {

  @BeforeEach
  public void setUp() {
    this.setUpScope();
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = ArcBasisMill.scope();
    }
    return this.scope;
  }
}