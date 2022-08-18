/* (c) https://github.com/MontiCore/monticore */
package generic;

import de.montiarc.runtimes.timesync.delegation.DelayedPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The system under test is the component {@code Forwarder}. The black-box
 * tests ensure that the system produces the expected outputs.
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
    forwarder.init();

    // When
    Integer in = 5;
    forwarder.getPortInput().setValue(in);
    forwarder.getPortInput().update();
    forwarder.compute();
    forwarder.update();
    Integer out = forwarder.getPortOutput().getCurrentValue();

    // Then
    Assertions.assertEquals(in, out);
  }
}