/* (c) https://github.com/MontiCore/monticore */
package endconsumerpackage.TransitiveConsumerDiagram;

import consumerpackage.ConsumerDiagram.FirstConsumer;
import libpackage.LibDiagram.TypeWithInt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TransitiveConsumerTest {

  @Test
  void checkComponentIsUsable() {
    // Given
    TypeWithInt intHolder = new TypeWithInt();
    FirstConsumer firstConsumer = new FirstConsumer();
    LastConsumer lastConsumer = new LastConsumer();

    // When
    intHolder.setSomeInt(11);
    firstConsumer.setIntHolder(intHolder);
    lastConsumer.setFirstConsumer(firstConsumer);

    // Then
    Assertions.assertAll(
      () -> Assertions.assertEquals(11, intHolder.getSomeInt()),
      () -> Assertions.assertSame(intHolder, firstConsumer.getIntHolder()),
      () -> Assertions.assertSame(firstConsumer, lastConsumer.getFirstConsumer())
    );
  }
}
