/* (c) https://github.com/MontiCore/monticore */
package montiarc.datatypes.generics;

import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
public class PrimitiveTypeParameterBoundsTest {

  @Test
  void shouldGeneratePrimitiveTypeParameterBounds() {
    PrimitiveTypeParameterBoundsComp sut = new PrimitiveTypeParameterBoundsCompBuilder()
        .setName("sut")
        .build();

    assertThat(sut).isNotNull();
    assertThat(sut.getName()).isEqualTo("sut");
  }
}
