/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@JSimTest
class FieldReferencingTest {

  @Test
  void fieldHasCorrectValue() {
    // Given
    FieldReferencingCompImpl sut
      = (FieldReferencingCompImpl) new FieldReferencingCompBuilder().setName("sut").build();

    // Then
    Assertions.assertEquals(1, sut.field_x());
    Assertions.assertEquals(6, sut.field_y());
    Assertions.assertEquals(7, sut.field_z());
    Assertions.assertEquals(4, sut.field_w());
  }
}
