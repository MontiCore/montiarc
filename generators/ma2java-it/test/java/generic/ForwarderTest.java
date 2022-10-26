/* (c) https://github.com/MontiCore/monticore */
package generic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The system under test is the component {@code Forwarder}. The black-box tests
 * ensure that the system produces the expected outputs.
 */
public class ForwarderTest {
  /**
   * Black-box test: Ensures that the automaton produces the expected output.
   */
  @Test
  @DisplayName("Should produce expected outputs")
  public void shouldProduceExpectedOutput() {

    //Given
    Forwarder<Integer> forwarder = new Forwarder<>();
    forwarder.setUp();
    forwarder.init();

    // When
    Integer in = 5;
    forwarder.getInput().update(in);
    forwarder.compute();
    Integer out = forwarder.getOutput().getValue();
    forwarder.tick();

    // Then
    Assertions.assertEquals(in, out);
  }
}