/* (c) https://github.com/MontiCore/monticore */
package montiarc.maunit.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FailingComponentTest {

  @Test
  public void testFail() {
    FailingComponentComp sut = new FailingComponentCompBuilder().setName("sut").build();
    try {
      sut.run(3);
    } catch (AssertionError e) {
      return;
    }
    Assertions.fail("Expected Component to throw an assertions error");
  }
}
