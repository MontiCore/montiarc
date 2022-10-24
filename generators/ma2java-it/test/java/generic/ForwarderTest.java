/* (c) https://github.com/MontiCore/monticore */
package generic;

import montiarc.rte.timesync.DelayedPort;
import montiarc.rte.timesync.Port;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import types.OnOff;

/**
 * The system under test is the component {@code Forwarder}. The black-box tests
 * ensure that the system produces the expected outputs.
 */
public class ForwarderTest {
  /**
   * Black-box test: Ensures that the automaton produces the expected output.
   */
  @Test
  @DisplayName("Component with hierarchy should produce expected outputs")
  public void shouldProduceExpectedOutput() {

    //Given
    Forwarder<Integer> forwarder = new Forwarder<>();
    forwarder.setUp(new DelayedPort<>(), new DelayedPort<>());

    // When
    Integer in = 5;
    ((Port<Integer>) forwarder.getInput()).setValue(in);
    ((Port<Integer>) forwarder.getInput()).update();
    forwarder.compute();
    forwarder.update();
    Integer out = forwarder.getOutput().getValue();

    // Then
    Assertions.assertEquals(in, out);
  }
}