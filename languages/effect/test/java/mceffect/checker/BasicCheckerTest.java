/* (c) https://github.com/MontiCore/monticore */
package mceffect.checker;

import org.junit.jupiter.api.Test;

public class BasicCheckerTest extends AbstractCheckerTest {


  @Test
  public void testBasicEffectChecker() {
    // Given
    String main = "checker.B";

    // When
    init("checker/BasicEffect.eff", main);

    // Then
    checkEffect(0, EffectCheckResult.Status.UNKNOWN, false);
    checkEffect(1, EffectCheckResult.Status.CORRECT, false);
  }
}
