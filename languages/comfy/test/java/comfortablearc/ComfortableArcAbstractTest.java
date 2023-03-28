/* (c) https://github.com/MontiCore/monticore */
package comfortablearc;

import arcbasis.ArcBasisAbstractTest;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;

public class ComfortableArcAbstractTest extends ArcBasisAbstractTest {

  @Override
  @BeforeEach
  public void setUp() {
    Log.clearFindings();
    ComfortableArcMill.globalScope().clear();
    ComfortableArcMill.reset();
    ComfortableArcMill.init();
    ComfortableArcAbstractTest.addBasicTypes2Scope();
  }
}
