/* (c) https://github.com/MontiCore/monticore */
package composition;

import Types.ChildType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * The system under test is the component {@code SubtypeConnection}. The
 * black-box tests ensure that the system produces the expected outputs.
 */
public class SubtypeConnectionTest {

  /**
   * Black-box test: Ensures that the topology of subcomponents produces the
   * expected outputs.
   */
  @Test
  @DisplayName("Component with subcomponent should produce expected outputs")
  public void shouldProduceExpectedOutput() {
    //Given
    SubtypeConnection component = new SubtypeConnection();
    component.setUp();
    component.init();
    ChildType in = new ChildType();

    // When
    component.getI().update(in);
    component.compute();

    // Then
    Assertions.assertEquals(in, component.getO().getValue());
  }
}
