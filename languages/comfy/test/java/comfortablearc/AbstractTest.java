/* (c) https://github.com/MontiCore/monticore */
package comfortablearc;

public class AbstractTest extends arcbasis.AbstractTest {

  @Override
  public void init() {
    ComfortableArcMill.globalScope().clear();
    ComfortableArcMill.reset();
    ComfortableArcMill.init();
    AbstractTest.addBasicTypes2Scope();
  }
}
