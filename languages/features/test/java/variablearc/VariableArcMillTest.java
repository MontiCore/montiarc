/* (c) https://github.com/MontiCore/monticore */
package variablearc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import variablearc._symboltable.IVariableArcGlobalScope;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The class under test is {@link VariableArcMill}.
 */
class VariableArcMillTest extends VariableArcTestBase {

  @AfterEach
  void reinitMill() {
    VariableArcMill.init();
  }

  @Test
  void shouldClosePresenceContextOnReset() {
    // Given
    IVariableArcGlobalScope globalScope = VariableArcMill.globalScope();
    globalScope.init(false);
    assertThat(globalScope.getPresenceContext()).as("context before the reset").isPresent();

    // When
    VariableArcMill.reset();

    // Then
    assertThat(globalScope.getPresenceContext()).isEmpty();
  }
}
