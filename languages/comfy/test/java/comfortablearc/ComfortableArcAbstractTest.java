/* (c) https://github.com/MontiCore/monticore */
package comfortablearc;

import arcbasis.ArcBasisAbstractTest;

public class ComfortableArcAbstractTest extends ArcBasisAbstractTest {

  @Override
  public void init() {
    ComfortableArcMill.globalScope().clear();
    ComfortableArcMill.reset();
    ComfortableArcMill.init();
    ComfortableArcAbstractTest.addBasicTypes2Scope();
  }
}
