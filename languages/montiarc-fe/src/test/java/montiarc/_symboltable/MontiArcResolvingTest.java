/* (c) https://github.com/MontiCore/monticore */
package montiarc._symboltable;

import arcbasis._symboltable.ArcBasisResolvingTest;
import montiarc.MontiArcMill;
import org.junit.jupiter.api.BeforeAll;

public class MontiArcResolvingTest extends ArcBasisResolvingTest {

  @BeforeAll
  public static void init() {
    MontiArcMill.init();
  }
}