/* (c) https://github.com/MontiCore/monticore */
package montiarc;

import montiarc._symboltable.IMontiArcGlobalScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link MontiArcMill}.
 */
class MontiArcMillTest extends MontiArcTestBase {

  @AfterEach
  void reinitMill() {
    MontiArcMill.init();
  }

  @Test
  void shouldClosePresenceContextOnReset() {
    // Given
    IMontiArcGlobalScope globalScope = MontiArcMill.globalScope();
    globalScope.init(false);
    assertThat(globalScope.getPresenceContext()).as("context before the reset").isPresent();

    // When
    MontiArcMill.reset();

    // Then
    assertThat(globalScope.getPresenceContext()).isEmpty();
  }
}
