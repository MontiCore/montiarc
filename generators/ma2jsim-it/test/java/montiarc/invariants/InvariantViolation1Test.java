/* (c) https://github.com/MontiCore/monticore */
package montiarc.invariants;

import de.se_rwth.commons.logging.Log;
import montiarc.rte.port.PortObserver;
import montiarc.rte.tests.JSimTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@JSimTest
class InvariantViolation1Test {

  @Test
  void testIO() {
    // Given
    InvariantViolation1Comp sut = new InvariantViolation1CompBuilder().setName("sut").build();
    PortObserver<Integer> port_o = new PortObserver<>();

    sut.port_o().connect(port_o);

    // When
    sut.run(1);

    // Then
    assertThat(Log.getFindings()).isNotEmpty();
    assertThat(Log.getFindings().size()).isEqualTo(1);
  }
}
