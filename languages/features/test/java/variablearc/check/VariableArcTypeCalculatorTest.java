/* (c) https://github.com/MontiCore/monticore */
package variablearc.check;

import arcbasis._symboltable.IArcBasisScope;
import arcbasis.check.AbstractArcTypeCalculatorTest;
import arcbasis.check.IArcTypeCalculator;
import de.monticore.types.check.TypeCheckResult;
import org.junit.jupiter.api.BeforeEach;
import variablearc.VariableArcMill;

/**
 * Holds test for methods of {@link VariableArcTypeCalculator}.
 *
 * @see AbstractArcTypeCalculatorTest for basic tests methods.
 */
public class VariableArcTypeCalculatorTest extends AbstractArcTypeCalculatorTest {

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
  protected IArcTypeCalculator getTypeCalculator() {
    if (this.typeCalculator == null) {
      this.typeCalculator = new VariableArcTypeCalculator(new TypeCheckResult());
    }
    return this.typeCalculator;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = VariableArcMill.scope();
    }
    return this.scope;
  }
}
