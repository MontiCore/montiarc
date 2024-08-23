/* (c) https://github.com/MontiCore/monticore */
package foopackage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FooCompTest {

  @Test
  public void shouldIncreaseInput() {
    // Given
    FooTest comp = new FooTest();
    comp.setUp();
    comp.init();

    // When
    comp.getInPort().update(10);
    comp.compute();

    // Then
    Assertions.assertEquals(11, comp.getOutPort().getValue());
  }
}
