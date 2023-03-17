/* (c) https://github.com/MontiCore/monticore */
package endconsumerpackage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TransitiveConsumerTest {

  @Test
  public void checkComponentIsUsable() {
    // Given
    TransitiveConsumer comp = new TransitiveConsumer();
    comp.setUp();
    comp.init();

    // When
    comp.getIncoming().update(10);
    comp.compute();

    // Then
    Assertions.assertEquals(11, comp.getOutgoing().getValue());
  }
}
