/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.IArcBasisScope;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.TypeCheckResult;
import org.junit.jupiter.api.BeforeEach;

/**
 * Holds test for methods of {@link ArcBasisDeriveType}.
 *
 * @see AbstractArcDeriveTypeTest for basic tests methods.
 */
public class ArcBasisDeriveTypeTest extends AbstractArcDeriveTypeTest {

  @Override
  @BeforeEach
  public void init() {
    ArcBasisMill.globalScope().clear();
    ArcBasisMill.reset();
    ArcBasisMill.init();
    addBasicTypes2Scope();
    this.setUp();
  }

  @Override
  protected IDerive getDerive() {
    if (this.derive == null) {
      this.derive = new ArcBasisDeriveType(new TypeCheckResult());
    }
    return this.derive;
  }

  @Override
  protected IArcBasisScope getScope() {
    if (this.scope == null) {
      this.scope = ArcBasisMill.scope();
    }
    return this.scope;
  }
}