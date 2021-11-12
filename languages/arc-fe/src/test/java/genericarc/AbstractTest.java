/*
 * (c) https://github.com/MontiCore/monticore
 */

package genericarc;

public class AbstractTest extends arcbasis.AbstractTest {

  @Override
  public void init() {
    GenericArcMill.globalScope().clear();
    GenericArcMill.reset();
    GenericArcMill.init();
    addBasicTypes2Scope();
  }
}
