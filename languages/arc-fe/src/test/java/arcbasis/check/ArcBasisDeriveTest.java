/* (c) https://github.com/MontiCore/monticore */
package arcbasis.check;

import arcbasis.ArcBasisMill;
import arcbasis._symboltable.IArcBasisScope;
import de.monticore.types.check.TypeCheckResult;
import montiarc.util.check.IArcDerive;
import org.junit.jupiter.api.BeforeAll;

/**
 * Holds test for methods of {@link ArcBasisDerive}.
 *
 * @see AbstractArcDeriveTest for basic tests methods.
 */
public class ArcBasisDeriveTest extends AbstractArcDeriveTest {

  @BeforeAll
  public static void initMill() {
    ArcBasisMill.init();
  }

  @Override
  protected IArcDerive getDerive() {
    if (this.derive == null) {
      this.derive = new ArcBasisDerive(new TypeCheckResult());
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