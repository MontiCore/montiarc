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
    intHolder.someInt = 11;
    firstConsumer.intHolder = intHolder;
    lastConsumer.firstConsumer =firstConsumer;

    // Then
    Assertions.assertEquals(11, intHolder.someInt);
    Assertions.assertSame(intHolder, firstConsumer.intHolder);
    Assertions.assertSame(firstConsumer, lastConsumer.firstConsumer);
  }
}
