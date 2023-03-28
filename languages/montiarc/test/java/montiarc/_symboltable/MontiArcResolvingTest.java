/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ArcBasisResolvingTest;
import montiarc.MontiArcMill;
import org.junit.jupiter.api.BeforeEach;

public class MontiArcResolvingTest extends ArcBasisResolvingTest {

  @BeforeEach
  @Override
  public void setUp() {
    MontiArcMill.globalScope().clear();
    MontiArcMill.init();
    super.setUpType();
  }
}