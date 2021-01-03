/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.IArcBasisScope;
import de.monticore.types.check.TypeCheckResult;
import montiarc.util.check.IArcTypesCalculator;

/**
 * Holds test for methods of {@link ArcBasisTypesCalculator}.
 *
 * @see AbstractArcTypesCalculatorTest for basic tests methods.
 */
public class ArcBasisTypesCalculatorTest extends AbstractArcTypesCalculatorTest {

  @Override
  protected IArcTypesCalculator getTypesCalculator() {
    if (this.typesCalculator == null) {
      this.typesCalculator = new ArcBasisTypesCalculator(new TypeCheckResult());
    }
    return this.typesCalculator;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = ArcBasisMill.arcBasisScopeBuilder().build();
    }
    return this.scope;
  }
}